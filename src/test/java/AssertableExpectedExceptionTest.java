import in.assertee.junit.AssertableExpectedException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.results.PrintableResult;
import org.junit.experimental.results.ResultMatchers;

/**
 * Created by Narendra Pathai on 1/3/2015.
 */
public class AssertableExpectedExceptionTest {

    public static class EmptyTestThrowsNoException {
        @Rule public AssertableExpectedException exception = AssertableExpectedException.none();

        @Test
        public void test() {

        }
    }

    @Test
    public void testShouldSucceed_AsNoExceptionIsExpectedAndNoneIsThrown() {
        Assert.assertThat(PrintableResult.testResult(EmptyTestThrowsNoException.class), ResultMatchers.isSuccessful());
    }

    public static class EmptyTestFailsAsExpectedToThrowException {
        @Rule public AssertableExpectedException exception = AssertableExpectedException.none();

        @Test
        public void test() {
            exception.expect(Exception.class);
        }
    }

    @Test
    public void testShouldFail_AsExceptionIsExpectedAndNoneIsThrown() {
        Assert.assertThat(PrintableResult.testResult(EmptyTestFailsAsExpectedToThrowException.class), ResultMatchers.failureCountIs(1));
    }

    public static class ThrowsExpectedException {
        @Rule public AssertableExpectedException exception = AssertableExpectedException.none();

        @Test
        public void test() {
            exception.expect(NullPointerException.class);
            throw new NullPointerException();
        }
    }

    @Test
    public void testShouldSucceed_AsExpectedExceptionIsThrown() {
        Assert.assertThat(PrintableResult.testResult(ThrowsExpectedException.class), ResultMatchers.isSuccessful());
    }

    public static class ThrowsExceptionBeforeExpected {
        @Rule public AssertableExpectedException exception = AssertableExpectedException.none();

        @Test
        public void test() {
            Integer val = null;
            int valI = val;
            exception.expect(NullPointerException.class);
        }
    }

    @Test
    public void testShouldFail_AsExceptionIsThrownBeforeExpected() {
        Assert.assertThat(PrintableResult.testResult(ThrowsExceptionBeforeExpected.class), ResultMatchers.failureCountIs(1));
    }

    public static class ThrowsDifferentException {
        @Rule AssertableExpectedException exception = AssertableExpectedException.none();

        @Test
        public void test() {
            exception.expect(NullPointerException.class);
            throw new IllegalStateException();
        }
    }

    @Test
    public void testShouldFail_AsDifferentExceptionIsThrownThanExpectedOne() {
        Assert.assertThat(PrintableResult.testResult(ThrowsDifferentException.class), ResultMatchers.failureCountIs(1));
    }

    public static class WhenClauseWithoutException {
        @Rule public AssertableExpectedException exception = AssertableExpectedException.none();

        @Test
        public void test() {
            exception.when(() -> {
                System.out.println("Hello world");});
        }

        @Test
        public void testFailure() {
            exception.when(() -> {throw new NullPointerException();});
        }
    }

    @Test
    public void testWhen() {
        Assert.assertThat(PrintableResult.testResult(WhenClauseWithoutException.class), ResultMatchers.failureCountIs(1));
    }

    public static class WhenClauseThrowsExpectedException {
        @Rule public AssertableExpectedException exception = AssertableExpectedException.none();

        @Test
        public void test() {
            exception.expect(NullPointerException.class)
                    .when(() -> {throw new NullPointerException();});
        }
    }

    @Test
    public void testShouldFail_AsWhenLogicThrowsExpectedException() {
        Assert.assertThat(PrintableResult.testResult(WhenClauseThrowsExpectedException.class), ResultMatchers.isSuccessful());
    }

    public static class WhenClauseThrowsUnexpectedException {
        @Rule public AssertableExpectedException exception = AssertableExpectedException.none();

        @Test
        public void test() {
            exception.expect(NullPointerException.class)
                    .when(() -> {throw new IllegalStateException();});
        }
    }

    @Test
    public void testShouldFail_AsWhenLogicThrowsDifferentExceptionThanExpected() {
        Assert.assertThat(PrintableResult.testResult(WhenClauseThrowsUnexpectedException.class), ResultMatchers.failureCountIs(1));
    }

    public static class State {
        private static final short EXCEPTION = -1;
        private int state;

        public void throwException() {
            this.state = EXCEPTION;
            throw new NullPointerException();
        }

        public int getState() {
            return state;
        }
    }

    public static class SuccessfulAssertionPostException {
        @Rule public AssertableExpectedException exception = AssertableExpectedException.none();
        private State state;

        @Before
        public void setUp() {
            this.state  = new State();
        }

        @Test
        public void test() {
            exception.expect(NullPointerException.class)
                    .when(() -> {state.throwException();})
                    .thenAssertThat(() -> {return state.getState();},
                            CoreMatchers.equalTo(-1));
        }
    }

    @Test
    public void testShouldSucceed_AsPostExceptionAssertionCriteriaPasses() {
        Assert.assertThat(PrintableResult.testResult(SuccessfulAssertionPostException.class), ResultMatchers.isSuccessful());
    }

    public static class FailedAssertionPostException {
        @Rule public AssertableExpectedException exception = AssertableExpectedException.none();
        private State state;

        @Before
        public void setUp() {
            this.state  = new State();
        }

        @Test
        public void test() {
            exception.expect(NullPointerException.class)
                    .when(() -> {state.throwException();})
                    .thenAssertThat(() -> {return state.getState();},
                            CoreMatchers.equalTo(100));
        }
    }

    @Test
    public void testShouldFail_AsPostExceptionAssertionCriteriaDoesNotPass() {
        Assert.assertThat(PrintableResult.testResult(FailedAssertionPostException.class), ResultMatchers.failureCountIs(1));
    }
}
