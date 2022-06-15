package simsim.ipfilter;

import java.time.Duration;
import java.util.*;

class BenchResult {
    private String name;
    private Map<String, Long> values = new HashMap<>();

    private List<Job> jobs = new ArrayList<>();

    public BenchResult(String name) {
        this.name = name;
    }

    public void add(Job job) {
        jobs.add(job);
    }

    public String formattedString() {
        StringBuilder builder = new StringBuilder();
        builder.append("name : ").append(name).append("\n");
        for (Job job : jobs) {
            builder.append(job.formattedString()).append("\n");
        }
        return builder.toString();
    }

    public String getName() {
        return name;
    }

    public void put(String prop, long value) {
        values.put(prop, value);
    }

    public Long get(String prop) {
        return values.get(prop);
    }

    static class Job {
        private String name;
        private long start;
        private long finish;

        public Job(String name, long start, long finish) {
            this.name = name;
            this.start = start;
            this.finish = finish;
        }

        public String getName() {
            return name;
        }

        public Duration getElapsed() {
            long elapsed = finish - start;
            Duration duration = Duration.ofMillis(elapsed);
            return duration;
        }

        public String formattedString() {
            Duration duration = getElapsed();
            return String.format("job: %s, elapsed: %s", name, duration);
        }
    }
}
