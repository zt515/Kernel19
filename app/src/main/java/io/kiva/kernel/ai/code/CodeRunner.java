package io.kiva.kernel.ai.code;

import com.dragon.extension.DragonNativeMethod;
import com.dragon.interpreter.DragonInterpreter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * @author kiva
 */

public class CodeRunner {

    private static DragonInterpreter newInterpreter() {
        return new DragonInterpreter();
    }

    public static void runCode(String init, String code, Object[] nativeInterfaces, CodeResultCallback callback) {
        DragonInterpreter interpreter = newInterpreter();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputStream);

        interpreter.setOut(out);
        interpreter.setErr(out);

        try {
            if (nativeInterfaces != null) {
                for (Object nativeInterface : nativeInterfaces) {
                    interpreter.linkNativeMethod(DragonNativeMethod.wrapJavaMethod(nativeInterface));
                }
            }

            interpreter.eval((init == null ? "" : init + ";\n") + code);
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
