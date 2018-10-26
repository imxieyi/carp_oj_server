package org.ai.carp.runner;

import org.ai.carp.model.judge.CARPCase;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;

public class JudgeWorker {

    public int maxJobs = 0;
    public List<CARPCase> jobs = new ArrayList<>();
    public WebSocketSession session;

}
