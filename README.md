junit-assertableexpectedexceptionrule
=====================================

An experimental clone of JUnit ExpectedException rule that allows for post exception assertions

This small rule is helpful in situations when there are some post asserts that need to be made on
the system under test, after the exception is thrown.

Up until JUnit 4.12 we do something like below

```java
@Test
public void test() {
  try {
    classUnderTest.methodThrowingException(); // perform action that is expected to throw exception
    throw new AssertionError("This must not occur"); // we forget this which causes false positives
  } catch (Exception e) {
    // make assertions on classUnderTest
  }
}
```

With this rule we can also specify the assertions after the exception is thrown. A simple test will 
look like this in Java 8

```java
public class Test {
  @Rule public AssertableExpectedException exception = AssertableExpectedException.none();
  
  @Test
  public void test() {
      exception.expect(Exception.class)
            .when(() -> {classUnderTest.methodThrowingException();})
            .thenAssertThat(() -> {return classUnderState.getState();},
                    CoreMatchers.equalTo(-1)); 
  }
}
```

So in this way this `Rule` allows post exception assertions possible which looks neat with Java 8 syntax.
Plus the `AssertableExpectedException` rule provides fluent interface for creating assertions.
