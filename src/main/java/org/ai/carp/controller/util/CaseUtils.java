package org.ai.carp.controller.util;

import org.ai.carp.model.Database;
import org.ai.carp.model.dataset.BaseDataset;
import org.ai.carp.model.judge.BaseCase;
import org.ai.carp.model.judge.CARPCase;
import org.ai.carp.model.judge.IMPCase;
import org.ai.carp.model.judge.ISECase;
import org.ai.carp.model.user.User;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.Optional;

public class CaseUtils {

    public static int countPreviousDay(User user) {
        Date oneDayBefore = new Date(new Date().getTime() - 24 * 3600 * 1000);
        return Database.getInstance().getCarpCases()
                .countCARPCasesByUserAndSubmitTimeAfter(user, oneDayBefore);
    }
    
    public static boolean checkResult(BaseCase baseCase) {
        if (baseCase.getStatus() != BaseCase.FINISHED) {
            return false;
        }
        if (baseCase.isTimedout()) {
            baseCase.setReason("Timed out");
            return false;
        }
        if (baseCase.getExitcode() != 0) {
            baseCase.setReason("Exit code is not zero");
            return false;
        }
        if (StringUtils.isEmpty(baseCase.getStdout())) {
            baseCase.setReason("No output");
            return false;
        }
        return true;
    }

    public static BaseCase saveCase(BaseCase baseCase) {
        switch (baseCase.getType()) {
            case BaseDataset.CARP:
                return Database.getInstance().getCarpCases().save((CARPCase) baseCase);
            case BaseDataset.ISE:
                return Database.getInstance().getIseCases().save((ISECase) baseCase);
            case BaseDataset.IMP:
                return Database.getInstance().getImpCases().save((IMPCase) baseCase);
            default:
        }
        return baseCase;
    }

    public static BaseCase findById(String cid) {
        Optional<CARPCase> optionalCARPCase = Database.getInstance().getCarpCases().findById(cid);
        if (optionalCARPCase.isPresent()) {
            return optionalCARPCase.get();
        }
        Optional<ISECase> optionalISECase = Database.getInstance().getIseCases().findById(cid);
        if (optionalISECase.isPresent()) {
            return optionalISECase.get();
        }
        Optional<IMPCase> optionalIMPCase = Database.getInstance().getImpCases().findById(cid);
        return optionalIMPCase.orElse(null);
    }

}
