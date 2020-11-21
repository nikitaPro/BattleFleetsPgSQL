package com.nctc2017.services.utils;

public class AutoDecisionTask implements Runnable {
    private int delay;
    private Visitor decisionVisitor;
    private long endTimePoint;

    public AutoDecisionTask(Visitor decisionVisitor, int delay) {
            this.decisionVisitor = decisionVisitor;
            this.delay = delay;
            this.endTimePoint = System.currentTimeMillis() + delay;
    }
    
    public long getTimeLeft() {
        return endTimePoint - System.currentTimeMillis();
    }

    public long getEndTimePoint() {
        return endTimePoint;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            return;
        }
        decisionVisitor.visit();
    }

}
