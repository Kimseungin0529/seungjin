package com.project.doongdoong.module;

import jakarta.validation.constraints.NotNull;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class TestExecutionTimeListener implements TestExecutionListener {
    private Long startTime;
    private static Long totalTime = 0L;

    @Override
    public void beforeTestClass(@NotNull TestContext testContext) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public void afterTestClass(@NotNull TestContext testContext) {
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;
        System.out.println("컨텍스트 로딩을 포함한 전체 테스트 실행 시간: " + executionTime + "ms");

        totalTime += executionTime;
        System.out.println("지금까지 걸린 시간: " + totalTime + "ms");
    }
}
