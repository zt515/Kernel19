package io.kiva.kernel.ai.code;

import com.dragon.interpreter.DragonBuiltinInterface;
import com.dragon.interpreter.DragonInterpreter;
import com.dragon.lang.ast.EvalError;
import com.dragon.lang.io.SystemIOBridge;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

/**
 * @author kiva
 */

public class CodeRunner {

    private static DragonInterpreter newInterpreter() {
        return new DragonInterpreter();
    }


    public static void runCode(String code, CodeResultCallback callback) {
        DragonInterpreter interpreter = newInterpreter();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputStream);

        interpreter.setOut(out);
        interpreter.setErr(out);

        try {
            interpreter.eval(code);
        } catch (Throwable throwable) {
            throwable.printStackTrace(out);
        } finally {
            try {
                outputStream.flush();
                String output = outputStream.toString("utf-8");
                outputStream.close();

                callback.onCodeFinished(output);
            } catch (UnsupportedEncodingException ignore) {
                // ignore
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
