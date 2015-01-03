package in.assertee.junit;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Assert;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Created by Narendra Pathai on 1/3/2015.
 */
public class AssertableExpectedException implements TestRule {
    private List<PostAssertion<?>> assertions = new ArrayList<>();
    private Class<? extends Throwable> expectedException;
    private Runnable runnable;

    public AssertableExpectedException(Class<? extends Throwable> expectedException) {
        this.expectedException = expectedException;
    }

    public static AssertableExpectedException none() {
        return new AssertableExpectedException(null);
    }

    @Override
    public Statement apply(final Statement statement, final Description description) {
        return new ExpectedExceptionStatement(statement);
    }

    public AssertableExpectedException expect(Class<? extends Throwable> expectedException) {
        this.expectedException = expectedException;
        return this;
    }

    public AssertableExpectedException when(Runnable run) {
        this.runnable = run;
        return this;
    }

    public <T> void  thenAssertThat(Supplier<T> supplier, Matcher<T> matcher) {
        this.assertions.add(new PostAssertion<T>(supplier, matcher));
    }

    class PostAssertion<T> {
        Supplier<T> supplier;
        Matcher<T> matcher;

        PostAssertion(Supplier<T> supplier, Matcher<T> matcher) {
            this.matcher = matcher;
            this.supplier = supplier;
        }

        private void doAssert() {
            Assert.assertThat(supplier.get(), matcher);
        }
    }

    private class ExpectedExceptionStatement extends Statement {
        private Statement statement;

        public ExpectedExceptionStatement(Statement statement) {
            this.statement = statement;
        }

        @Override
        public void evaluate() throws Throwable {
            try {
                statement.evaluate();
                if (runnable != null) {
                    runnable.run();
                }
            } catch (Throwable t) {
                if (expectedException == null) {
                    Assert.fail("Did not expect to throw any exception");
                } else {
                    Assert.assertThat(t, CoreMatchers.instanceOf(expectedException));
                    postExceptionAssertions();
                    return;
                }
            }
            if (expectedException != null) {
                Assert.fail("Expected to throw exception");
            }
        }

        private void postExceptionAssertions() {
            for (PostAssertion<?> assertion : assertions) {
                assertion.doAssert();
            }
        }
    }
}
