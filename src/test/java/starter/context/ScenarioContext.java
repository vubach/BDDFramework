package starter.context;

public class ScenarioContext {
	
	private ThreadLocal<TestContext> testContext = new ThreadLocal<TestContext>();

    public ScenarioContext() {
        this.testContext.set(new TestContext());
    }

    public void setContext(String key, Object value) {
        this.testContext.get().setData(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getContext(String key) {
        return (T) this.testContext.get().getData(key);
    }

    public Boolean isContains(String key) {
        return this.testContext.get().isContains(key);
    }
}
