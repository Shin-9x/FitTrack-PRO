package it.fartingbrains.fitness.common.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger _log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("@annotation(it.fartingbrains.fitness.common.annotation.Loggable)")
    public Object logRestMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String paramNamesAndValues = formatParams(
                (MethodSignature) joinPoint.getSignature(),
                joinPoint.getArgs()
        );

        long startTime = System.currentTimeMillis();

        if (_log.isInfoEnabled()) {
            _log.info("[{}] START method {} with params: {}", methodName, methodName, paramNamesAndValues);
        }

        try {
            Object result = joinPoint.proceed();

            if (result instanceof Mono<?>) {
                return ((Mono<?>) result)
                        .doOnSuccess(res -> logEnd(methodName, startTime))
                        .doOnError(error -> logError(methodName, startTime, error));
            } else if (result instanceof Flux<?>) {
                return ((Flux<?>) result)
                        .doOnComplete(() -> logEnd(methodName, startTime))
                        .doOnError(error -> logError(methodName, startTime, error));
            } else {
                logEnd(methodName, startTime);
                return result;
            }
        } catch (Throwable t) {
            logError(methodName, startTime, t);
            throw t;
        }
    }

    private void logEnd(String methodName, long startTime) {
        long executionTime = System.currentTimeMillis() - startTime;

        if (_log.isInfoEnabled()) {
            _log.info("[{}] END method {}. Execution time [{} ms]", methodName, methodName, executionTime);
        }
    }

    private void logError(String methodName, long startTime, Throwable throwable) {
        long executionTime = System.currentTimeMillis() - startTime;

        if (_log.isErrorEnabled()) {
            _log.error(
                    String.format(
                            "[%s] ERROR occurred in method [%s]. Execution Time [%d ms]",
                            methodName, methodName, executionTime
                    ),
                    throwable
            );
        }
    }

    private String formatParams(MethodSignature methodSignature, Object[] methodArgs) {
        String[] paramNames = methodSignature.getParameterNames();
        return IntStream
                .range(0, paramNames.length)
                .mapToObj(i -> paramNames[i] + ": [" + methodArgs[i] + "]")
                .collect(Collectors.joining(", "));
    }
}
