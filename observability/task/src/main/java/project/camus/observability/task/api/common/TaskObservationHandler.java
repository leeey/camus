package project.camus.observability.task.api.common;

import io.micrometer.observation.Observation.Context;
import io.micrometer.observation.ObservationHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TaskObservationHandler implements ObservationHandler<Context> {

    @Override
    public void onStart(Context context) {

        log.info("Before running the observation for context [{}], keyValues [{}]",
            context.getName(), context.getAllKeyValues());
    }

    @Override
    public void onStop(Context context) {

        log.info("After running the observation for context [{}], keyValues [{}]",
            context.getName(), context.getAllKeyValues());
    }

    @Override
    public boolean supportsContext(@NonNull Context context) {

        return true;
    }
}
