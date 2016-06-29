package com.hartwig.healthchecks.smitty.report;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import com.hartwig.healthchecks.common.report.BaseDataReport;
import com.hartwig.healthchecks.common.util.BaseReport;
import com.hartwig.healthchecks.common.util.CheckType;

public class KinshipReport extends BaseReport {

    private static final long serialVersionUID = -5744830259786248569L;

    private final List<BaseDataReport> patientData;

    public KinshipReport(final CheckType checkType, final List<BaseDataReport> knishipData) {
        super(checkType);
        patientData = knishipData;
    }

    @NotNull
    public List<BaseDataReport> getPatientData() {
        return patientData;
    }

    @Override
    @NotNull
    public String toString() {
        return "KinshipReport [knishipData=" + patientData + "]";
    }
}