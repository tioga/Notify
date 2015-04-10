package org.tiogasolutions.notify.kernel.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Created by harlan on 2/28/15.
 */
@Test
public class JsOldRouteEvaluatorTest {
  private static final Logger log = LoggerFactory.getLogger(JsOldRouteEvaluatorTest.class);

  public void evalTest() throws ScriptException, NoSuchMethodException {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    Invocable invocable = (Invocable) engine;

    // Convert route to JSON.
    Map<String, String> traitMap = new HashMap<>();
    traitMap.put("one", "dog");

    String jsFunc = "var testFunc = function (topic, traitMap) { return topic == 'main' && traitMap.one == \"dog\"}";
    engine.eval(jsFunc);
    boolean result = (boolean)invocable.invokeFunction("testFunc", "main", traitMap);
    assertTrue(result);
    result = (boolean)invocable.invokeFunction("testFunc", "junk", traitMap);
    assertFalse(result);


  }

  public void evalTest2() throws ScriptException, NoSuchMethodException {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    Invocable invocable = (Invocable) engine;

    // Convert route to JSON.
    Map<String, String> traitMap = new HashMap<>();
    traitMap.put("one", "dog");

//    String jsFunc = "var testFunc = function (object) { print(\"JS Class Definition: \" + object.key); return true}";
//    String jsFunc = "var testFunc = function (traitMap, topic) { return topic != 'main' && traitMap.one == \"dog\"}";
//    String jsFunc = "var testFunc = function (traitMap, topic) { return true}";
    String jsFunc = "function (traitMap, topic) { return false;}";
    engine.eval(jsFunc);
    EvalFunc evalFunc = invocable.getInterface(EvalFunc.class);
    boolean result = evalFunc.eval(traitMap, "main");
//    boolean result = (boolean)invocable.invokeFunction("testFunc", traitMap, "main");

    log.info("Result: " + result);

    assertTrue(result);


  }

  public void evalTest3() throws ScriptException, NoSuchMethodException {
    ScriptEngine engine = new ScriptEngineManager().getEngineByName("nashorn");
    Invocable invocable = (Invocable) engine;

    // Convert route to JSON.
    Map<String, String> traitMap = new HashMap<>();
    traitMap.put("one", "dog");

    StringBuilder js = new StringBuilder();
    js.append("var testFunc = function (object) { print(\"JS Class Definition: \" + object.key); return true};");
    js.append("var eval1 = function (topic, traitMap) { return topic == 'main' && traitMap.one == \"dog\"};");
    js.append("var eval2 = function (topic, traitMap) { return topic != 'main' && traitMap.one == \"dog\"};");
    js.append("var eval3 = function (traitMap, topic) { return false}");

    engine.eval(js.toString());
//    EvalFunc evalFunc = invocable.getInterface(EvalFunc.class);
    boolean result = (boolean)invocable.invokeFunction("testFunc", "main", traitMap);
    assertTrue(result);
    result = (boolean)invocable.invokeFunction("eval1", "main", traitMap);
    assertTrue(result);
    result = (boolean)invocable.invokeFunction("eval2", "main", traitMap);
    assertFalse(result);
    result = (boolean)invocable.invokeFunction("eval3", "main", traitMap);
    assertFalse(result);


  }

  public interface EvalFunc {
    boolean eval(Map<String, String> traits, String topic);
  }


}
