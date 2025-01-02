package de.ddm.example;

public class WorkerResult {
    public final boolean isDependencyValid;
    public final String comparisonInfo;

    public WorkerResult(boolean isDependencyValid, String comparisonInfo) {
        this.isDependencyValid = isDependencyValid;
        this.comparisonInfo = comparisonInfo;
    }
}
