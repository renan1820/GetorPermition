package com.gitproject.getorpermition.data.repository;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import com.gitproject.getorpermition.data.model.AppInfo;
import com.gitproject.getorpermition.data.model.PermissionInfo;
import com.gitproject.getorpermition.utils.PermissionClassifier;
import com.gitproject.getorpermition.utils.RiskCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PermissionRepository {

    public interface ScanCallback {
        /** Called on background thread for progress updates */
        void onProgress(String currentAppName, int scanned, int total);
        /** Called on main thread when scan is finished */
        void onComplete(List<AppInfo> apps, int globalScore);
        /** Called on main thread if an error occurs */
        void onError(Exception e);
    }

    private final Context context;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public PermissionRepository(Context context) {
        this.context = context.getApplicationContext();
    }

    public void scanInstalledApps(ScanCallback callback) {
        executor.execute(() -> {
            try {
                PackageManager pm = context.getPackageManager();

                // GET_PERMISSIONS retrieves each app's declared permissions
                List<PackageInfo> packages = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS);

                // Filter out system apps — only show user-installed apps
                List<PackageInfo> userApps = new ArrayList<>();
                for (PackageInfo pkg : packages) {
                    if (!isSystemApp(pkg)) {
                        userApps.add(pkg);
                    }
                }

                int total = userApps.size();
                List<AppInfo> result = new ArrayList<>();

                for (int i = 0; i < userApps.size(); i++) {
                    PackageInfo pkg = userApps.get(i);

                    String appName;
                    try {
                        appName = pm.getApplicationLabel(pkg.applicationInfo).toString();
                    } catch (Exception e) {
                        appName = pkg.packageName;
                    }

                    // Progress callback (still on background thread — ViewModel posts to LiveData)
                    callback.onProgress(appName, i + 1, total);

                    List<PermissionInfo> permissions = buildPermissionList(pkg);

                    AppInfo appInfo = new AppInfo(appName, pkg.packageName, permissions);

                    // Load icon safely
                    try {
                        Drawable icon = pm.getApplicationIcon(pkg.applicationInfo);
                        appInfo.setIcon(icon);
                    } catch (Exception ignored) { }

                    // Score calculation
                    appInfo.setRiskScore(RiskCalculator.calculateAppScore(appInfo));
                    result.add(appInfo);
                }

                // Sort descending by risk: most dangerous first (lowest score first)
                Collections.sort(result, (a, b) -> Integer.compare(a.getRiskScore(), b.getRiskScore()));

                int globalScore = RiskCalculator.calculateGlobalScore(result);

                mainHandler.post(() -> callback.onComplete(result, globalScore));

            } catch (Exception e) {
                mainHandler.post(() -> callback.onError(e));
            }
        });
    }

    private List<PermissionInfo> buildPermissionList(PackageInfo pkg) {
        List<PermissionInfo> list = new ArrayList<>();
        if (pkg.requestedPermissions == null) return list;

        for (String perm : pkg.requestedPermissions) {
            if (perm != null) {
                list.add(PermissionClassifier.classify(perm));
            }
        }

        // Sort: HIGH first, then MEDIUM, then LOW
        Collections.sort(list, (a, b) -> a.getRiskLevel().ordinal() - b.getRiskLevel().ordinal());
        return list;
    }

    // An app is considered a system app if it's installed in /system partition
    private boolean isSystemApp(PackageInfo pkg) {
        return (pkg.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }
}
