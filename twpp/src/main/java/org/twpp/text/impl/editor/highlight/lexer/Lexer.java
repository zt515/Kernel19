package org.twpp.text.impl.editor.highlight.lexer;


import org.twpp.text.adapter.TokenizeAdapter;
import org.twpp.text.impl.editor.common.DocumentProvider;
import org.twpp.text.lexer.Flag;
import org.twpp.text.lexer.Span;

import java.util.List;

public class Lexer {

    /**
     * 词法分析回调
     */
    LexCallback callback = null;

    /**
     * 词法分析适配器
     */
    private TokenizeAdapter tokenizeAdapter = new TokenizeAdapter() {
        @Override
        public List<Span> tokenize(Flag flag, String documentProvider) {
            return DEFAULT_SPANS;
        }
    };

    /**
     * 要词法分析的文本提供器
     */
    private DocumentProvider hDoc;

    /**
     * 词法分析线程
     */
    private LexThread workerThread = null;

    /**
     * @return 词法分析适配器
     */
    public TokenizeAdapter getTokenizeAdapter() {
        return tokenizeAdapter;
    }

    /**
     * 设置词法分析适配器
     * @param tokenizeAdapter
     */
    public void setTokenizeAdapter(TokenizeAdapter tokenizeAdapter) {
        this.tokenizeAdapter = tokenizeAdapter;
    }

    /**
     * 构造器
     * @param callback 解析回调
     */
    public Lexer(LexCallback callback) {
        this.callback = callback;
    }

    public void tokenize(DocumentProvider hDoc) {

        //使用副本,不然会影响渲染
        setDocument(new DocumentProvider(hDoc));
        if (workerThread == null) {
            workerThread = new LexThread();
            workerThread.start();
        } else {
            workerThread.restart();
        }
    }

    /**
     * 提醒回调词法分析完成
     * @param result 词法分析结果
     */
    void notifyTokenizeDone(List<Span> result) {
        if (result == null){
            result = TokenizeAdapter.DEFAULT_SPANS;
        }
        if (callback != null) {
            callback.lexDone(result);
        }
        workerThread = null;
    }

    /**
     * 立即取消词法分析
     * NOTE: 需要词法分析适配器支持
     */
    public void cancelTokenize() {
        if (workerThread != null) {
            workerThread.abort();
            workerThread = null;
        }
    }

    /**
     * @return 要词法分析的文档提供器
     */
    public synchronized DocumentProvider getDocument() {
        return hDoc;
    }

    /**
     * 设置文本内容
     * @param hDoc 文本内容
     */
    public synchronized void setDocument(DocumentProvider hDoc) {
        this.hDoc = hDoc;
    }


    /**
     * 词法分析监听器
     */
    public interface LexCallback {

        /**
         * 词法分析完成
         * @param results 词法分析结果
         */
        void lexDone(List<Span> results);
    }

    /**
     * 词法分析的工作线程
     */
    private class LexThread extends Thread {

        private final Flag isAbort;
        private boolean rescan = false;

        private List<Span> tokenCollection;

        public LexThread() {
            isAbort = new Flag();
        }

        @Override
        public void run() {
            do {
                rescan = false;
                isAbort.clear();
                tokenize();
            }
            while (rescan);

            if (!isAbort.isSet()) {
                //词法分析完成
                notifyTokenizeDone(tokenCollection);
            }
        }

        /**
         * 重新开始词法分析
         */
        public void restart() {
            rescan = true;
            isAbort.set();
        }

        /**
         * 终止词法分析
         */
        public void abort() {
            isAbort.set();
        }

        /**
         * 执行词法分析
         */
        public void tokenize() {
           this.tokenCollection = tokenizeAdapter.tokenize(isAbort,hDoc.getText());
        }


    }
}
