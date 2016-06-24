package com.hartwig.healthchecks.boggs.model.report;

import java.util.ArrayList;
import java.util.List;

import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

import org.jetbrains.annotations.NotNull;

public class PrestatsReport extends BaseReport {

    private static final long serialVersionUID = 3588650481644358694L;

    @NotNull
    private final List<BaseDataReport> summary = new ArrayList<>();

    public PrestatsReport(@NotNull final CheckType checkType) {
        super(checkType);
    }

    public void addData(@NotNull final BaseDataReport prestatsDataReport) {
        summary.add(prestatsDataReport);
    }

    public void addAllData(@NotNull final List<BaseDataReport> prestatsDataReport) {
        summary.addAll(prestatsDataReport);
    }

    @NotNull
    public List<BaseDataReport> getSummary() {
        return summary;
    }

    @Override
    public String toString() {
        return "PrestatsReport [summary=" + summary + "]";
    }
}
