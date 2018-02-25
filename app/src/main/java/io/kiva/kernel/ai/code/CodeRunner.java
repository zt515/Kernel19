package io.kiva.kernel.ai.code;

import com.krine.extension.IKrineLinkable;
import com.krine.extension.KrineExtension;
import com.krine.interpreter.KrineInterpreter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * @author kiva
 */

public class CodeRunner {

    private static KrineInterpreter newInterpreter() {
        return new KrineInterpreter();
    }

    public static void runCode(String init, String code, Class<? extends IKrineLinkable>[] nativeInterfaces, CodeResultCallback callback) {
        KrineInterpreter interpreter = newInterpreter();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outputStream);

        interpreter.setOut(out);
        interpreter.setErr(out);

        try {
            if (nativeInterfaces != null) {
                for (Class<? extends IKrineLinkable> nativeInterface : nativeInterfaces) {
                    interpreter.linkNativeInterface(KrineExtension.fromClass(nativeInterface));
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
