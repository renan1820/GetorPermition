package com.gitproject.getorpermition.ui.scan;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Validates ScanViewModel.ScanState enum definition and transition semantics.
 *
 * ScanViewModel itself requires Android Application context (AndroidViewModel)
 * so it is tested via instrumented tests. Here we only validate the state contract.
 */
public class ScanStateTest {

    @Test
    public void scanState_hasExactlyFourValues() {
        assertEquals(4, ScanViewModel.ScanState.values().length);
    }

    @Test
    public void scanState_idleExists() {
        assertNotNull(ScanViewModel.ScanState.valueOf("IDLE"));
    }

    @Test
    public void scanState_scanningExists() {
        assertNotNull(ScanViewModel.ScanState.valueOf("SCANNING"));
    }

    @Test
    public void scanState_doneExists() {
        assertNotNull(ScanViewModel.ScanState.valueOf("DONE"));
    }

    @Test
    public void scanState_errorExists() {
        assertNotNull(ScanViewModel.ScanState.valueOf("ERROR"));
    }

    @Test
    public void scanState_idleIsFirstState() {
        // IDLE must be the initial state (ordinal 0)
        assertEquals(0, ScanViewModel.ScanState.IDLE.ordinal());
    }

    @Test
    public void scanState_allValuesAreDistinct() {
        ScanViewModel.ScanState[] values = ScanViewModel.ScanState.values();
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertNotEquals("States must be distinct", values[i], values[j]);
            }
        }
    }
}
