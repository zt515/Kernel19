package org.twpp.text.impl.editor.ui.internal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.ClipboardManager;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.widget.OverScroller;

import org.twpp.text.Language;
import org.twpp.text.impl.editor.common.Document;
import org.twpp.text.impl.editor.common.DocumentProvider;
import org.twpp.text.impl.editor.highlight.lexer.Lexer;
import org.twpp.text.impl.editor.model.Pair;
import org.twpp.text.impl.editor.ui.SelectionModeChangeListener;
import org.twpp.text.lexer.Span;
import org.twpp.text.lexer.TokenType;
import org.twpp.text.listener.OnAutoCompletionListener;
import org.twpp.text.listener.OnEditActionListener;
import org.twpp.text.skin.LightSkin;
import org.twpp.text.skin.Skin;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;


public class EditWidget extends View {

    /**
     * 光标跳动的时间
     */
    private static final long CURSOR_JUMP_TIME = 600L;
    /**
     * 空字符宽度相对于正常字符宽度的比例
     */
    protected static float EMPTY_Cursor_WIDTH_SCALE = 0.75f;
    /**
     * 默认的Tab所占空格数
     */
    protected static int DEFAULT_TAB_LENGTH_SPACES = 4;
    protected static int BASE_TEXT_SIZE_PIXELS = 16;
    /**
     * 滚动控制器
     */
    private final OverScroller scroller;
    /**
     * 绘制文本使用的画笔
     */
    private Paint textPaint;
    /**
     * 行号所占边距
     */
    protected int leftPadding;
    /**
     * 触摸行为
     */
    protected TouchNavigationMethod touchNavigationMethod;
    /**
     * 提供文本内容
     */
    protected DocumentProvider doc;
    /**
     * 当前光标的位置
     */
    protected int cursorPosition = 0;
    /**
     * 当前选择高亮的左侧偏移
     */
    protected int selectionLeft = -1;
    /**
     * 当前选择高亮的右侧偏移
     */
    protected int selectionRight = -1;
    /**
     * Tab所占空格数
     */
    protected int _tabLength = DEFAULT_TAB_LENGTH_SPACES;
    /**
     * Tab所占空格对应的文本
     */
    protected String tabSpaceContent = makeTabs();
    /**
     * 主题
     */
    protected Skin skin = LightSkin.getInstance();
    /**
     * 是否高亮当前所在行
     */
    protected boolean isHighlightCurrentLine = false;
    /**
     * 是否开启自动缩进
     */
    protected boolean isAutoIndent = true;
    /**
     * 编辑行为控制器
     */
    private EditBehaviorController editBehaviorController;
    /**
     * 与输入法的连接
     */
    private TextFieldInputConnection inputConnection;
    /**
     * 光标所在行改变监听器
     */
    private LineChangeListener lineChangeListener;
    /**
     * 光标所在行改变监听器
     */
    private SelectionModeChangeListener selectionModeChangeListener;
    /**
     * 光标所在行
     */
    private int cursorLine = 0;

    /**
     * 最长的一行的宽度
     */
    private int maxTextWidth = 0;
    /**
     * 缓存advances,这样就不用每次都计算
     */
    private int[] advances;
    /**
     * 是否可以编辑
     */
    private boolean editable = true;
    /**
     * 用于控制光标显示和消失的变量
     */
    private boolean showCursor;
    /**
     * 一个控制光标显示和消失的线程
     */
    private CursorLooperThread CursorLooperThread;
    /**
     *
     */
    private OnEditActionListener onEditActionListener;
    /**
     * 可以滑动的X轴空白范围
     */
    private float freeScrollSpaceX = 100;
    /**
     * 可以滑动的Y轴空白返回
     */
    private float freeScrollSpaceY = 600;

    private Language language = new Language();

    private boolean cursorVisible = true;

    public boolean isCursorVisible() {
        return cursorVisible;
    }

    public void setCursorVisible(boolean cursorVisible) {
        this.cursorVisible = cursorVisible;
    }


    public EditWidget(Context context) {
        this(context, null);
    }

    public EditWidget(Context context, AttributeSet attrs) {
        super(context, attrs);

        //默认的监听器
        lineChangeListener = new LineChangeListener() {
            @Override
            public void onLineChange(int newLineIndex) {
            }
        };

        selectionModeChangeListener = new SelectionModeChangeListener() {
            @Override
            public void onSelectionModeChanged(boolean active) {
            }
        };

        doc = new DocumentProvider();
        touchNavigationMethod = new TouchNavigationMethod(this);
        scroller = new OverScroller(context);
        initView();
        CursorLooperThread = new CursorLooperThread(new WeakReference<EditWidget>(this));
        CursorLooperThread.start();
    }


    public EditWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        lineChangeListener = new LineChangeListener() {
            @Override
            public void onLineChange(int newLineIndex) {
                // Do nothing
            }
        };

        selectionModeChangeListener = new SelectionModeChangeListener() {
            @Override
            public void onSelectionModeChanged(boolean active) {
                // Do nothing
            }
        };
        doc = new DocumentProvider();
        touchNavigationMethod = new TouchNavigationMethod(this);
        scroller = new OverScroller(context);
        initView();
    }

    public float getTextSize() {
        return textPaint.getTextSize();
    }


    public void setTextSize(float textSize) {
        this.textPaint.setTextSize(textSize);
        updateLeftPadding();
        editBehaviorController.updateCursorLine();
        if (!makeCharVisible(cursorPosition)) {
            invalidate();
        }
    }

    public void release() {
        if (CursorLooperThread != null) {
            CursorLooperThread.interrupt();
            CursorLooperThread = null;
        }

    }

    protected void initView() {
        freeScrollSpaceX = 1000;
        freeScrollSpaceY = 1000;
        this.advances = new int[128];
        Arrays.fill(this.advances, -1);
        editBehaviorController = this.new EditBehaviorController();

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(BASE_TEXT_SIZE_PIXELS);

        setBackgroundColor(skin.getColor(Skin.Colorable.BACKGROUND));
        setLongClickable(false);
        setFocusableInTouchMode(true);
        setHapticFeedbackEnabled(true);

        resetView();
        setScrollContainer(true);
    }


    public TouchNavigationMethod getTouchMethod() {
        return touchNavigationMethod;
    }

    public boolean canUndo() {
        return doc.canUndo();
    }

    public boolean canRedo() {
        return doc.canRedo();
    }

    /**
     * 后退
     *
     * @return 数量
     */
    public int undo() {
        int result = doc.undo();

        invalidate();
        refreshSpans();
        updateLeftPadding();

        return result;
    }

    /**
     * 重做
     *
     * @return 数量
     */
    public int redo() {
        int result = doc.redo();

        invalidate();
        refreshSpans();
        updateLeftPadding();

        return result;
    }

    /**
     * 重置视图
     */
    private void resetView() {
        cursorPosition = 0;
        cursorLine = 0;
        maxTextWidth = 0;
        editBehaviorController.setSelectText(false);
        editBehaviorController.stopTextComposing();
        doc.clearSpans();
        lineChangeListener.onLineChange(0);
        scrollTo(0, 0);
    }

    /**
     * 设置新的内容提供器
     */
    public void setDocumentProvider(DocumentProvider hDoc) {
        doc = hDoc;
        doc.setOnEditActionListener(onEditActionListener);
        resetView();
        editBehaviorController.cancelSpanning(); //stop existing lex threads
        editBehaviorController.refreshSpans();
        invalidate();
    }


    /**
     * @return 当前内容提供器的副本
     */
    public DocumentProvider cloneDocumentProvider() {
        return new DocumentProvider(doc);
    }

    /**
     * @return 文本长度
     */
    public int length() {
        return doc.docLength();
    }

    /**
     * 设置行改变监听器
     *
     * @param lineChangeListener 行改变监听器
     */
    public void setLineChangeListener(LineChangeListener lineChangeListener) {
        this.lineChangeListener = lineChangeListener;
    }

    /**
     * 设置选择模式改变监听器
     *
     * @param selectionModeChangeListener 选择模式改变监听器
     */
    public void setSelModeListener(SelectionModeChangeListener selectionModeChangeListener) {
        this.selectionModeChangeListener = selectionModeChangeListener;
    }

    /**
     *
     */
    public void setNavigationMethod(TouchNavigationMethod navMethod) {
        touchNavigationMethod = navMethod;
    }

    /**
     * 在指定位置插入一段文本
     * @param pos 位置
     * @param text 要插入的文本
     */
    public void insert(int pos, String text) {
        doc.insertBefore(text.toCharArray(), pos, System.nanoTime());
        updateLeftPadding();
        refreshSpans();
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        outAttrs.inputType = InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_FLAG_MULTI_LINE;
        outAttrs.imeOptions = EditorInfo.IME_FLAG_NO_ENTER_ACTION
                | EditorInfo.IME_ACTION_DONE
                | EditorInfo.IME_FLAG_NO_EXTRACT_UI;
        if (inputConnection == null) {
            inputConnection = this.new TextFieldInputConnection(this);
        } else {
            inputConnection.resetComposingState();
        }
        return inputConnection;
    }

    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    public boolean isSaveEnabled() {
        return true;
    }

    //---------------------------------------------------------------------
    //------------------------- Layout 方法 ----------------------------
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(useAllDimensions(widthMeasureSpec),
                useAllDimensions(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        editBehaviorController.updateCursorLine();
        if (!makeCharVisible(cursorPosition)) {
            invalidate();
        }
    }

    private int useAllDimensions(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int result = MeasureSpec.getSize(measureSpec);

        if (specMode != MeasureSpec.EXACTLY && specMode != MeasureSpec.AT_MOST) {
            result = Integer.MAX_VALUE;
        }

        return result;
    }

    /**
     * @return 屏幕可见的行数
     */
    public int getVisibleLines() {
        return (int) Math.ceil((double) getContentHeight() / lineHeight());
    }

    /**
     * @return 一行的高度
     */
    public int lineHeight() {
        Paint.FontMetricsInt metrics = textPaint.getFontMetricsInt();
        return (metrics.descent - metrics.ascent);
    }

    /**
     * @return 内容高度
     */
    protected int getContentHeight() {
        return getHeight() - getPaddingTop() - getPaddingBottom();
    }

    /**
     * @return 内容宽度
     */
    protected int getContentWidth() {
        return getWidth() - getPaddingLeft() - getPaddingRight();
    }

    /**
     *
     * @return 左边距
     */
    @Override
    public int getPaddingLeft() {
        return super.getPaddingLeft() + this.leftPadding;
    }

    /**
     * @return 行号宽度
     */
    public int getLineNumberPadding() {
        return leftPadding;
    }

    /**
     * @return 可见的第一行
     */
    private int getBeginPaintLine(Canvas canvas) {
        Rect bounds = canvas.getClipBounds();
        if (bounds.top == 0) {
            bounds.top = 1;
        }
        return bounds.top / lineHeight();
    }

    /**
     * @return 可见的最后一行
     */
    private int getEndPaintLine(Canvas canvas) {
        Rect bounds = canvas.getClipBounds();
        return (bounds.bottom - 1) / lineHeight();
    }

    /**
     * @return 给定行的行基址
     */
    private int getPaintBaseline(int line) {
        Paint.FontMetricsInt metrics = textPaint.getFontMetricsInt();
        return (line + 1) * lineHeight() - metrics.descent;
    }

    /**
     * 完整绘制流程
     *
     * @param canvas 画布
     */
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.clipRect(getScrollX() + super.getPaddingLeft(),
                getScrollY() + getPaddingTop(),
                (getScrollX() + getWidth()) - getPaddingRight()
                , (getScrollY() + getHeight()) - getPaddingBottom());
        canvas.translate(super.getPaddingLeft(), getPaddingTop());
        drawContent(canvas);
        canvas.restore();
        this.touchNavigationMethod.onTextDrawComplete(canvas);
    }


    public DocumentProvider getDoc(){
        return doc;
    }

    private void drawContent(Canvas canvas) {

        //可见第一行
        int beginPaintLine = getBeginPaintLine(canvas);

        //可见最后一行
        int endPaintLine = getEndPaintLine(canvas);

        //第一行的baseline
        int paintBaseline = getPaintBaseline(beginPaintLine);

        //已经绘制到的偏移量
        int currentOffset = this.doc.getLineOffset(beginPaintLine);

        if (currentOffset < 0) {
            return;
        }
        //如果开启了高亮当前所在行 并且光标在可见区域内, 画当前所在行的Rect
        if (this.isHighlightCurrentLine && beginPaintLine <= cursorLine && endPaintLine >= cursorLine) {
            this.textPaint.setColor(this.skin.getColor(Skin.Colorable.LINE_HIGHLIGHT));
            drawTextBackground(canvas, this.leftPadding, getPaintBaseline(cursorLine), getWidth());
        }

        //无论什么时候,List中至少有一个Span
        List<Span> spans = this.doc.getSpans();

        Span prevSpan;
        Span currSpan = spans.get(0);
        int currIndex;
        int nextIndex = 1;

        do {
            prevSpan = currSpan;
            if (nextIndex < spans.size()) {
                currIndex = nextIndex;
                currSpan = spans.get(currIndex);
                nextIndex++;
            } else {
                currSpan = null;
            }
            if (currSpan == null) {
                break;
            }
        } while (currSpan.getOffset() <= currentOffset);

        TokenType tokenType = prevSpan.getTokenType();
        int tokenColor = this.skin.getTokenColor(prevSpan);
        this.textPaint.setColor(tokenColor);
        int currentSpanOffset = currSpan != null ? currSpan.getOffset() : -1;

        //当前所在行的行号
        int currentLineNumber = this.doc.findLineNumber(currentOffset) + 1;

        while (beginPaintLine <= endPaintLine) {
            String replace = this.doc.getLine(beginPaintLine).replace(Language.EOF, '\u0000');
            if (replace.length() == 0) {
                break;
            }
            currentLineNumber++;

            //如果行号看不见,就不用画了
            if (this.leftPadding > getScrollX()) {
                    this.textPaint.setColor(Color.GRAY);
                    canvas.drawText(String.valueOf(currentLineNumber), 0.0f, paintBaseline, this.textPaint);
                    this.textPaint.setColor(tokenColor);
            }

            int lineExtend = this.leftPadding;


            int cur = 0;
            while (cur < replace.length()) {

                if (currentOffset == currentSpanOffset) {
                    tokenType = currSpan != null ? currSpan.getTokenType() : TokenType.NORMAL;
                    tokenColor = this.skin.getTokenColor(currSpan);
                    this.textPaint.setColor(tokenColor);
                    if (nextIndex < spans.size()) {
                        currIndex = nextIndex;
                        nextIndex++;
                        currSpan = spans.get(currIndex);
                        currentSpanOffset = currSpan.getOffset();
                    } else {
                        currSpan = null;
                        currentSpanOffset = -1;
                    }
                }
                if (currentOffset == this.cursorPosition) {
                    if (isFocused()) {
                        this.textPaint.setColor(this.skin.getColor(Skin.Colorable.CURSOR_BACKGROUND));
                    }else {
                        this.textPaint.setColor(this.skin.getColor(Skin.Colorable.CURSOR_DISABLED));
                    }
                    if (this.showCursor) {
                        drawTextBackground(canvas, lineExtend, paintBaseline, 2);
                    }
                    this.textPaint.setColor(tokenColor);
                }
                int min;
                if (inSelectionRange(currentOffset)) {

                    min = Math.min((getSelectionEnd() - currentOffset) + cur, replace.length());
                    if (this.cursorPosition > currentOffset) {
                        min = Math.min(min, (this.cursorPosition - currentOffset) + cur);
                    }
                    lineExtend += drawSelectedText(canvas,
                            replace.substring(cur, min).replace("\t", tabSpaceContent),
                            lineExtend,
                            paintBaseline);
                    currentOffset += min - cur;
                    cur = min - 1;

                    while (currentOffset > currentSpanOffset && currentSpanOffset != -1) {
                        if (nextIndex < spans.size()) {
                            currIndex = nextIndex;
                            nextIndex++;
                            currSpan = spans.get(currIndex);
                            currentSpanOffset = currSpan.getOffset();
                        } else {
                            currSpan = null;
                            currentSpanOffset = -1;
                        }
                    }
                } else {
                    min = replace.length();
                    if (currentSpanOffset > currentOffset) {
                        min = Math.min((currentSpanOffset - currentOffset) + cur, min);
                    }
                    if (isSelectText()) {
                        if (getSelectionStart() > currentOffset) {
                            min = Math.min(min, (getSelectionStart() - currentOffset) + cur);
                        }
                    } else if (this.cursorPosition > currentOffset) {
                        min = Math.min(min, (this.cursorPosition - currentOffset) + cur);
                    }
                    String replaceIndent = replace.substring(cur, min).replace("\t", tabSpaceContent);
                    
                    lineExtend += drawString(canvas, replaceIndent, lineExtend, paintBaseline, tokenType == TokenType.KEYWORD);
                    currentOffset += min - cur;
                    cur = min - 1;
                }
                cur++;
            }

            //准备画下一行
            paintBaseline += lineHeight();

            if (lineExtend > this.maxTextWidth) {
                this.maxTextWidth = lineExtend;
            }
            beginPaintLine++;
        }
    }

    class SpanIndexer {
        List<Span> spans;
        ListIterator<Span> spanIterator;
        Span currentSpan;
        int position;

        public SpanIndexer(List<Span> spans) {
            this.spans = spans;
        }

        public void toPosition(int position) {
            this.position = position;
            spanIterator = spans.listIterator();
            currentSpan = spanIterator.next();
            while (spanIterator.hasNext() && currentSpan.getOffset() < position) {
                currentSpan = spanIterator.next();
            }
            if (currentSpan.getOffset() < position) {
                currentSpan = null;
            }
        }

        public Span next() {
            if (spanIterator.hasNext()) {
                currentSpan = spanIterator.next();
            }else {
                currentSpan = null;
            }
            return currentSpan;
        }

        public Span prev() {
            if (spanIterator.hasPrevious()) {
                currentSpan = spanIterator.previous();
            }else {
                currentSpan = null;
            }
            return currentSpan;
        }
    }

    /**
     * 创建Tab所占空格的文本
     *
     * @return 空格字符串
     */
    private String makeTabs() {
        StringBuilder stringBuilder = new StringBuilder(_tabLength);
        for (int i = 0; i < _tabLength; i++) {
            stringBuilder.append(" ");
        }
        return stringBuilder.toString();
    }

    /**
     * 绘制选择区域的文本
     *
     * @param canvas 画布
     * @param text 选择范围内的文本
     * @param x 开始X轴
     * @param y 开始Y轴
     * @return 绘制的长度
     */
    private int drawSelectedText(final Canvas canvas, final String text, final int x, final int y) {
        final int color = this.textPaint.getColor();
        final int length = (int) this.textPaint.measureText(text);
        this.textPaint.setColor(this.skin.getColor(Skin.Colorable.SELECTION_BACKGROUND));
        this.drawTextBackground(canvas, x, y, length);
        this.textPaint.setColor(this.skin.getColor(Skin.Colorable.SELECTION_FOREGROUND));
        this.drawString(canvas, text, x, y,false);
        this.textPaint.setColor(color);
        return length;
    }

    /**
     * 绘制字符串
     *
     */
    private float drawString(final Canvas canvas, final String s, final float x, final float y,boolean isBold) {
        textPaint.setFakeBoldText(isBold);
        canvas.drawText(s, x, y, this.textPaint);
        textPaint.setFakeBoldText(false);
        return textPaint.measureText(s);
    }

    /**
     * 绘制文本背景
     *
     */
    private void drawTextBackground(Canvas canvas, int paintX, int paintY,
                                    int advance) {
        Paint.FontMetricsInt metrics = textPaint.getFontMetricsInt();
        canvas.drawRect(paintX,
                paintY + metrics.ascent,
                paintX + advance,
                paintY + metrics.descent,
                textPaint);
    }

    protected int getSpaceAdvance() {
        return (int) textPaint.measureText(" ", 0, 1);
    }

    protected int getEOLAdvance() {
        return (int) (EMPTY_Cursor_WIDTH_SCALE * textPaint.measureText(" ", 0, 1));
    }

    protected int getTabAdvance() {
        return _tabLength * (int) textPaint.measureText(" ", 0, 1);
    }

    /**
     * Invalidate rows from startRow (inclusive) to endRow (exclusive)
     */
    private void invalidateLines(int startLine, int endLine) {

        Rect cursorSpill = touchNavigationMethod.getCursorBloat();
        Paint.FontMetricsInt metrics = textPaint.getFontMetricsInt();
        int top = startLine * lineHeight() + getPaddingTop();
        top -= Math.max(cursorSpill.top, metrics.descent);
        top = Math.max(0, top);
        super.invalidate(0,
                top,
                getScrollX() + getWidth(),
                endLine * lineHeight() + getPaddingTop() + cursorSpill.bottom);
    }

    /**
     * 重绘指定行
     */
    private void invalidateFromLine(int lineNumber) {

        Rect CursorSpill = touchNavigationMethod.getCursorBloat();
        Paint.FontMetricsInt metrics = textPaint.getFontMetricsInt();
        int top = lineNumber * lineHeight() + getPaddingTop();
        top -= Math.max(CursorSpill.top, metrics.descent);
        top = Math.max(0, top);
        super.invalidate(0,
                top,
                getScrollX() + getWidth(),
                getScrollY() + getHeight());
    }


    //---------------------------------------------------------------------
    //-------------------滑动与触摸 -----------------------------

    private void invalidateCursorLine() {
        invalidateLines(cursorLine, cursorLine + 1);
    }

    private void invalidateSelectionLines() {
        int startLine = doc.findLineNumber(selectionLeft);
        int endLine = doc.findLineNumber(selectionRight);

        invalidateLines(startLine, endLine + 1);
    }

    /**
     * Scrolls the text horizontally and/or vertically if the character
     * specified by charOffset is not in the visible text region.
     * The view is invalidated if it is scrolled.
     *
     * @param charOffset The index of the character to make visible
     * @return True if the drawing area was scrolled horizontally
     * and/or vertically
     */
    private boolean makeCharVisible(int charOffset) {

        int scrollVerticalBy = makeCharLineVisible(charOffset);
        int scrollHorizontalBy = makeCharColumnVisible(charOffset);

        if (scrollVerticalBy == 0 && scrollHorizontalBy == 0) {
            return false;
        } else {
            scrollBy(scrollHorizontalBy, scrollVerticalBy);
            return true;
        }
    }

    /**
     * 计算到达指定字符偏移需要滚动的Y轴相对偏移如果指定的字符偏移不在视图可见范围内
     *
     * @param charOffset 字符偏移
     * @return 需要滚动的Y轴相对偏移
     */
    private int makeCharLineVisible(int charOffset) {
        int scrollBy = 0;
        int charTop = doc.findLineNumber(charOffset) * lineHeight();
        int charBottom = charTop + lineHeight();

        if (charTop < getScrollY()) {
            scrollBy = charTop - getScrollY();
        } else if (charBottom > (getScrollY() + getContentHeight())) {
            scrollBy = charBottom - getScrollY() - getContentHeight();
        }

        return scrollBy;
    }

    /**
     * 计算到达指定字符偏移需要滚动的X轴相对偏移如果指定的字符偏移不在视图可见范围内
     *
     * @param charOffset 字符偏移
     * @return 需要滚动的X轴相对偏移
     */
    private int makeCharColumnVisible(int charOffset) {
        int scrollBy = 0;
        Pair visibleRange = getCharExtent(charOffset);

        int charLeft = visibleRange.first;
        int charRight = visibleRange.second;

        if (charRight > (getScrollX() + getContentWidth())) {
            scrollBy = charRight - getScrollX() - getContentWidth();
        }

        if (charLeft < getScrollX()) {
            scrollBy = charLeft - getScrollX();
        }

        return scrollBy;
    }

    /**
     * Calculates the x-coordinate extent of charOffset.
     *
     * @return The x-values of left and right edges of charOffset. Pair.first
     * contains the left edge and Pair.second contains the right edge
     */
    protected Pair getCharExtent(int charOffset) {
        int line = doc.findLineNumber(charOffset);
        int currOffset = doc.seekChar(doc.getLineOffset(line));
        int left = 0;
        int right = 0;

        while (currOffset <= charOffset && doc.hasNext()) {
            left = right;
            char c = doc.next();
            switch (c) {
                case ' ':
                    right += getSpaceAdvance();
                    break;
                case Language.NEWLINE:
                case Language.EOF:
                    right += getEOLAdvance();
                    break;
                case Language.TAB:
                    right += getTabAdvance();
                    break;
                default:
                    char[] ca = {c};
                    right += (int) textPaint.measureText(ca, 0, 1);
                    break;
            }
            ++currOffset;
        }

        return new Pair(left, right);
    }

    /**
     *
     * @return 包含指定字符串偏移的Rect
     *
     * @param charOffset 字符串偏移量(位置)
     */
    Rect getBoundingBox(int charOffset) {
        if (charOffset < 0 || charOffset >= doc.docLength()) {
            return new Rect(-1, -1, -1, -1);
        }

        int line = doc.findLineNumber(charOffset);
        int top = line * lineHeight();
        int bottom = top + lineHeight();

        Pair xExtent = getCharExtent(charOffset);
        int left = xExtent.first;
        int right = xExtent.second;

        return new Rect(left, top, right, bottom);
    }

    public Skin getSkin() {
        return skin;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
        touchNavigationMethod.onColorSchemeChanged(skin);
        setBackgroundColor(skin.getColor(Skin.Colorable.BACKGROUND));
    }


    public int getFontHeight() {
        Paint.FontMetricsInt fontMetricsInt = this.textPaint.getFontMetricsInt();
        return fontMetricsInt.descent - fontMetricsInt.ascent;
    }

    /**
     * Maps a coordinate to the character that it is on. If the coordinate is
     * on empty space, the nearest character on the corresponding row is returned.
     * If there is no character on the row, -1 is returned.
     * <p/>
     * The coordinates passed in should not have padding applied to them.
     *
     * @param x x-coordinate
     * @param y y-coordinate
     * @return The index of the closest character, or -1 if there is
     * no character or nearest character at that coordinate
     */
    public int coordToCharIndex(int x, int y) {
        int line = y / lineHeight();
        int charIndex = doc.getLineOffset(line);

        if (charIndex < 0) {
            //non-existent row
            return -1;
        }

        if (x < 0) {
            return charIndex; // coordinate is outside, to the left of view
        }

        String lineText = doc.getLine(line);

        int extent = 0;
        int i = 0;
        while (i < lineText.length()) {
            char c = lineText.charAt(i);
            if (c == Language.NEWLINE || c == Language.EOF) {
                extent += getEOLAdvance();
            } else if (c == ' ') {
                extent += getSpaceAdvance();
            } else if (c == Language.TAB) {
                extent += getTabAdvance();
            } else {
                char[] ca = {c};
                extent += (int) textPaint.measureText(ca, 0, 1);
            }

            if (extent >= x) {
                break;
            }

            ++i;
        }

        if (i < lineText.length()) {
            return charIndex + i;
        }

        //nearest char is last char of line
        return charIndex + i - 1;
    }

    public int getAdvance(char c){
        int advance;

        switch (c){
            case ' ':
                advance = getSpaceAdvance();
                break;
            case Language.NEWLINE:
            case Language.EOF:
                advance = getEOLAdvance();
                break;
            case Language.TAB:
                advance = getTabAdvance();
                break;
            default:
                char[] ca = {c};
                advance = (int) textPaint.measureText(ca, 0, 1);
                break;
        }

        return advance;
    }

    /**
     * @param x 横坐标
     * @param y 纵坐标
     * @return 坐标对应的字符串
     */
    public int coordToCharIndexStrict(int x, int y) {
        int line = y / lineHeight();
        int charIndex = doc.getLineOffset(line);

        if (charIndex < 0 || x < 0) {
            //non-existent row
            return -1;
        }

        String lineText = doc.getLine(line);

        int extent = 0;
        int i = 0;
        while (i < lineText.length()) {
            char c = lineText.charAt(i);
            if (c == Language.NEWLINE || c == Language.EOF) {
                extent += getEOLAdvance();
            } else if (c == ' ') {
                extent += getSpaceAdvance();
            } else if (c == Language.TAB) {
                extent += getTabAdvance();
            } else {
                char[] ca = {c};
                extent += (int) textPaint.measureText(ca, 0, 1);
            }

            if (extent >= x) {
                break;
            }

            ++i;
        }

        if (i < lineText.length()) {
            return charIndex + i;
        }

        return -1;
    }

    /**
     * @return X轴滑动极限
     */
    int getMaxScrollX() {
        return (int) Math.max(0,
                maxTextWidth - getContentWidth() + touchNavigationMethod.getCursorBloat().right + freeScrollSpaceX);
    }

    /**
     * @return Y轴滑动极限
     */
    int getMaxScrollY() {
        return (int) (Math.max(0,
                doc.getLineCount() * lineHeight() - getContentHeight() + touchNavigationMethod.getCursorBloat().bottom) + freeScrollSpaceY);
    }

    @Override
    protected int computeVerticalScrollOffset() {
        return getScrollY();
    }

    @Override
    protected int computeVerticalScrollRange() {
        return doc.getLineCount() * lineHeight() + getPaddingTop() + getPaddingBottom();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
        }
    }

    /**
     * 以指定加速度开始滑动
     */
    void flingScroll(int velocityX, int velocityY) {
        scroller.fling(getScrollX(), getScrollY(), velocityX, velocityY,
                0, getMaxScrollX(), 0, getMaxScrollY());
        postInvalidate();
    }

    /**
     * @return 是否正在滑动
     */
    public boolean isFlingScrolling() {
        return !scroller.isFinished();
    }

    /**
     * 如果正在滑动,强制停止滑动
     */
    public void stopFlingScrolling() {
        scroller.forceFinished(true);
    }

    public boolean autoScrollCursor(ScrollTarget scrollDir) {
        boolean scrolled = false;
        switch (scrollDir) {
            case SCROLL_UP:
                if ((!CursorOnFirstLineOfFile())) {
                    moveCursorUp();
                    scrolled = true;
                }
                break;
            case SCROLL_DOWN:
                if (!CursorOnLastLineOfFile()) {
                    moveCursorDown();
                    scrolled = true;
                }
                break;
            case SCROLL_LEFT:
                if (cursorPosition > 0 &&
                        cursorLine == doc.findLineNumber(cursorPosition - 1)) {
                    moveCursorLeft();
                    scrolled = true;
                }
                break;
            case SCROLL_RIGHT:
                if (!CursorOnEOF() &&
                        cursorLine == doc.findLineNumber(cursorPosition + 1)) {
                    moveCursorRight();
                    scrolled = true;
                }
                break;
            default:
                break;
        }
        return scrolled;
    }



    //---------------------------------------------------------------------
    //------------------------- Cursor methods -----------------------------

    public int getCursorLine() {
        return cursorLine;
    }

    public int getCursorPosition() {
        return cursorPosition;
    }

    /**
     * Sets the Cursor to position i, scrolls it to view and invalidates
     * the necessary areas for redrawing
     *
     * @param pos The character index that the Cursor should be set to
     */
    public void moveCursor(int pos) {
        if (pos > doc.docLength()) {
            pos = doc.docLength() - 1;
        }
        showCursor = true;
        boolean change = pos != cursorPosition;
        editBehaviorController.moveCursor(pos);
        if (change && onEditActionListener != null) {
            onEditActionListener.onUpdateCursor();
        }
    }

    /**
     * Sets the Cursor one position back, scrolls it on screen, and invalidates
     * the necessary areas for redrawing.
     * <p/>
     * If the Cursor is already on the first character, nothing will happen.
     */
    public void moveCursorLeft() {
        editBehaviorController.moveCursorLeft(false);
    }

    /**
     * Sets the Cursor one position forward, scrolls it on screen, and
     * invalidates the necessary areas for redrawing.
     * <p/>
     * If the Cursor is already on the last character, nothing will happen.
     */
    public void moveCursorRight() {
        editBehaviorController.moveCursorRight(false);
    }

    /**
     * Sets the Cursor one row down, scrolls it on screen, and invalidates the
     * necessary areas for redrawing.
     * <p/>
     * If the Cursor is already on the last row, nothing will happen.
     */
    public void moveCursorDown() {
        editBehaviorController.moveCursorDown();
    }

    /**
     * Sets the Cursor one row up, scrolls it on screen, and invalidates the
     * necessary areas for redrawing.
     * <p/>
     * If the Cursor is already on the first row, nothing will happen.
     */
    public void moveCursorUp() {
        editBehaviorController.moveCursorUp();
    }

    /**
     * Scrolls the Cursor into view if it is not on screen
     */
    public void focusCursor() {
        makeCharVisible(cursorPosition);
    }

    /**
     * @return 字符偏移所在的列
     */
    protected int getColumn(int charOffset) {
        int line = doc.findLineNumber(charOffset);
        int firstCharOfRow = doc.getLineOffset(line);
        return charOffset - firstCharOfRow;
    }

    protected boolean CursorOnFirstLineOfFile() {
        return (cursorLine == 0);
    }

    protected boolean CursorOnLastLineOfFile() {
        return (cursorLine == (doc.getLineCount() - 1));
    }

    protected boolean CursorOnEOF() {
        return (cursorPosition == (doc.docLength() - 1));
    }


    //---------------------------------------------------------------------
    //------------------------- Text Selection ----------------------------

    public final boolean isSelectText() {
        return editBehaviorController.isSelectText();
    }

    /**
     * Enter or exit select mode.
     * Invalidates necessary areas for repainting.
     *
     * @param mode If true, enter select mode; else exit select mode
     */
    public void selectText(boolean mode) {
        if (editBehaviorController.isSelectText() && !mode) {
            invalidateSelectionLines();
            editBehaviorController.setSelectText(false);
        } else if (!editBehaviorController.isSelectText() && mode) {
            invalidateCursorLine();
            editBehaviorController.setSelectText(true);
        }
    }

    public void selectAll() {
        editBehaviorController.setSelectionRange(0, doc.docLength() - 1, false);
    }

    public void setSelectionRange(int beginPosition, int numChars) {
        editBehaviorController.setSelectionRange(beginPosition, numChars, true);
    }

    public boolean inSelectionRange(int charOffset) {
        return editBehaviorController.inSelectionRange(charOffset);
    }

    public int getSelectionStart() {
        return selectionLeft;
    }

    public int getSelectionEnd() {
        return selectionRight;
    }

    public void focusSelectionStart() {
        editBehaviorController.focusSelection(true);
    }

    public void focusSelectionEnd() {
        editBehaviorController.focusSelection(false);
    }

    public void cut(ClipboardManager cb) {
        editBehaviorController.cut(cb);
    }

    public void copy(ClipboardManager cb) {
        editBehaviorController.copy(cb);
    }

    public void paste(String text) {
        editBehaviorController.paste(text);
        if (onEditActionListener != null) {
            onEditActionListener.onPaste(text);
        }
    }

    public Paint getTextPaint(){
        return textPaint;
    }


    public void cancelSpanning() {
        editBehaviorController.cancelSpanning();
    }

    //---------------------------------------------------------------------
    //------------------------- Formatting methods ------------------------

    /**
     * 设置字体
     */
    public void setTypeface(Typeface typeface) {
        Arrays.fill(this.advances, -1);
        textPaint.setTypeface(typeface);
        editBehaviorController.updateCursorLine();
        if (!makeCharVisible(cursorPosition)) {
            invalidate();
        }
    }


    /**
     * 从开始位置删除指定数量的字符串
     *
     * @param start
     * @param count
     */
    public void delete(int start, int count) {
        doc.deleteAt(start, count, System.nanoTime());
        updateLeftPadding();
        refreshSpans();
    }

    public boolean isEditable() {
        return editable;
    }

    public void setToViewMode() {
        editable = false;
    }

    public void setToEditMode() {
        editable = true;
    }

    /**
     * 设置字体大小
     */
    public void setZoom(float factor) {
        if (factor <= 0) {
            return;
        }
        Arrays.fill(this.advances, -1);
        this.maxTextWidth = 0;
        this.textPaint.setTextSize(factor * getContext().getResources().getDisplayMetrics().density);

        updateLeftPadding();
        editBehaviorController.updateCursorLine();
        if (!makeCharVisible(cursorPosition)) {
            invalidate();
        }
    }

    /**
     * 设置Tab所占空格数
     *
     * @param spaceCount Tab所占空格数
     */
    public void setTabSpaces(int spaceCount) {
        if (spaceCount < 0) {
            return;
        }
        _tabLength = spaceCount;
        editBehaviorController.updateCursorLine();
        tabSpaceContent = makeTabs();
        if (!makeCharVisible(cursorPosition)) {
            invalidate();
        }
    }

    /**
     * 开启/关闭 自动缩进
     */
    public void setAutoIndent(boolean enable) {
        isAutoIndent = enable;
    }

    /**
     * 设置是否高亮当前所在行
     */
    public void setHighlightCurrentLine(boolean enable) {
        isHighlightCurrentLine = enable;
        invalidateCursorLine();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (touchNavigationMethod.onKeyDown(keyCode, event)) {
            return true;
        }

        if (KeysInterpreter.isNavigationKey(event)) {
            handleNavigationKey(keyCode, event);
            return true;
        }


        char c = KeysInterpreter.keyEventToPrintableChar(event);
        if (c == Language.NULL_CHAR) {
            return super.onKeyDown(keyCode, event);
        }

        editBehaviorController.onPrintableChar(c);
        if (c != '\b' && onAutoCompletionListener != null) {
            onAutoCompletionListener.onPopCodeComplete(new String(new char[]{c}));
        }

        return true;
    }

    private void handleNavigationKey(int keyCode, KeyEvent event) {
        if (event.isShiftPressed() && !isSelectText()) {
            invalidateCursorLine();
            editBehaviorController.setSelectText(true);
        } else if (!event.isShiftPressed() && isSelectText()) {
            invalidateSelectionLines();
            editBehaviorController.setSelectText(false);
        }

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                editBehaviorController.moveCursorRight(false);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                editBehaviorController.moveCursorLeft(false);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                editBehaviorController.moveCursorDown();
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                editBehaviorController.moveCursorUp();
                break;
            default:
                break;
        }
    }

    public OnEditActionListener getOnEditActionListener() {
        return onEditActionListener;
    }

    public void setOnEditActionListener(OnEditActionListener onEditActionListener) {
        this.onEditActionListener = onEditActionListener;
        doc.setOnEditActionListener(onEditActionListener);
    }

    public void updateLeftPadding() {
        leftPadding = (int) textPaint.measureText(String.valueOf(doc.getLineCount() + " "));
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return touchNavigationMethod.onKeyUp(keyCode, event) || super.onKeyUp(keyCode, event);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isFocused()) {
            touchNavigationMethod.onTouchEvent(event);
        } else {
            if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP
                    && isPointInView((int) event.getX(), (int) event.getY())) {
                requestFocus();
            }
        }
        return true;
    }

    /**
     * 点击的位置是否在编辑器中?
     *
     * @param x X坐标
     * @param y Y坐标
     * @return 点击的位置是否在编辑器中
     */
    public boolean isPointInView(int x, int y) {
        return (x >= 0 && x < getWidth() &&
                y >= 0 && y < getHeight());
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        invalidateCursorLine();
    }

    /**
     * Not public to allow access by {@link TouchNavigationMethod}
     */
    public void showIME(boolean show) {
        InputMethodManager im = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);

        if (!editable) {
            im.hideSoftInputFromWindow(this.getWindowToken(), 0);
            return;
        }

        if (show) {
            im.showSoftInput(this, 0);
        } else {
            im.hideSoftInputFromWindow(this.getWindowToken(), 0);
        }
    }

    /**
     * @return 当前的UI状态
     */
    public Parcelable getUiState() {
        return new TextFieldUiState(this);
    }

    /**
     * 还原UI状态
     *
     * @param state 要还原的UI状态
     */
    public void restoreUiState(Parcelable state) {
        TextFieldUiState uiState = (TextFieldUiState) state;
        final int CursorPosition = uiState.cursorPosition;
        if (uiState.selectMode) {
            final int selStart = uiState.selectBegin;
            final int selEnd = uiState.selectEnd;

            post(new Runnable() {
                @Override
                public void run() {
                    setSelectionRange(selStart, selEnd - selStart);
                    if (CursorPosition < selEnd) {
                        focusSelectionStart();
                    }
                }
            });
        } else {
            post(new Runnable() {
                @Override
                public void run() {
                    moveCursor(CursorPosition);
                }
            });
        }
    }

    public void refreshSpans() {
        editBehaviorController.refreshSpans();
    }

    /**
     * @return 当前文本内容
     */
    public String getText() {
        return new String(doc.subSequence(0, doc.docLength() - 1));
    }

    /**
     * 设置文本内容
     *
     * @param text 文本内容
     */
    public void setText(String text) {
        Document document = new Document();
        document.insert(text.toCharArray(), 0, 0, false);
        DocumentProvider documentProvider = new DocumentProvider(document);
        setDocumentProvider(documentProvider);
        moveCursor(0);
        refreshSpans();
        updateLeftPadding();
    }

    public void append(String text) {
        paste(text);
    }

    /**
     * 设置语言
     *
     * @param language 语言
     */
    public void setLanguage(Language language) {
        this.language = language;
        editBehaviorController.lexer.setTokenizeAdapter(language);
    }

    /**
     * 光标滑动标识
     */
    enum ScrollTarget {
        SCROLL_UP,
        SCROLL_DOWN,
        SCROLL_LEFT,
        SCROLL_RIGHT
    }



    OnAutoCompletionListener onAutoCompletionListener;

    public OnAutoCompletionListener getOnAutoCompletionListener() {
        return onAutoCompletionListener;
    }

    public void setOnAutoCompletionListener(OnAutoCompletionListener onAutoCompletionListener) {
        this.onAutoCompletionListener = onAutoCompletionListener;
    }


    /**
     * 一个光标循环线程
     */
    static class CursorLooperThread extends Thread {
        WeakReference<EditWidget> ref;

        public CursorLooperThread(WeakReference<EditWidget> ref) {
            this.ref = ref;
        }

        @Override
        public void run() {
            super.run();
            for (; ; ) {
                EditWidget textField = ref.get();
                if (textField == null) {
                    break;
                }
                textField.showCursor = textField.cursorVisible && !textField.isSelectText() && !textField.showCursor && textField.isFocused();
                textField.post(new Runnable() {
                    @Override
                    public void run() {
                        EditWidget textField = ref.get();
                        if (!textField.isFlingScrolling()) {
                            textField.invalidateCursorLine();
                        }
                        //不要去掉赋值null,否则内存泄露
                        textField = null;
                    }
                });
                //不要去掉赋值null,否则内存泄露
                textField = null;
                try {
                    Thread.sleep(CURSOR_JUMP_TIME);
                } catch (InterruptedException ignored) {
                }
            }
        }
    }

    //*********************************************************************
    //************************ 控制器逻辑 ***************************
    //*********************************************************************

    //*********************************************************************
    //**************** 保存和还原UI状态 ******************
    //*********************************************************************

    public static class TextFieldUiState implements Parcelable {
        public static final Creator<TextFieldUiState> CREATOR
                = new Creator<TextFieldUiState>() {
            @Override
            public TextFieldUiState createFromParcel(Parcel in) {
                return new TextFieldUiState(in);
            }

            @Override
            public TextFieldUiState[] newArray(int size) {
                return new TextFieldUiState[size];
            }
        };
        final int cursorPosition;
        final int scrollX;
        final int scrollY;
        final boolean selectMode;
        final int selectBegin;
        final int selectEnd;

        public TextFieldUiState(EditWidget editWidget) {
            cursorPosition = editWidget.getCursorPosition();
            scrollX = editWidget.getScrollX();
            scrollY = editWidget.getScrollY();
            selectMode = editWidget.isSelectText();
            selectBegin = editWidget.getSelectionStart();
            selectEnd = editWidget.getSelectionEnd();
        }

        private TextFieldUiState(Parcel in) {
            cursorPosition = in.readInt();
            scrollX = in.readInt();
            scrollY = in.readInt();
            selectMode = in.readInt() != 0;
            selectBegin = in.readInt();
            selectEnd = in.readInt();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeInt(cursorPosition);
            out.writeInt(scrollX);
            out.writeInt(scrollY);
            out.writeInt(selectMode ? 1 : 0);
            out.writeInt(selectBegin);
            out.writeInt(selectEnd);
        }

    }

    private class EditBehaviorController
            implements Lexer.LexCallback {
        /**
         * 词法分析器
         */
        private Lexer lexer = new Lexer(this);
        {
            lexer.setTokenizeAdapter(language);
        }
        private boolean isInSelectionMode = false;

        /**
         * Analyze the text for programming language keywords and redraws the
         * text view when done. The global programming language used is set with
         * the static method Lexer.setLanguage(Language)
         * <p/>
         * Does nothing if the Lexer language is not a programming language
         */
        public void refreshSpans() {
            lexer.tokenize(doc);
        }

        public void cancelSpanning() {
            lexer.cancelTokenize();
        }

        @Override
        public void lexDone(final List<Span> results) {
            post(new Runnable() {
                @Override
                public void run() {
                    doc.setSpans(results);
                    invalidate();
                }
            });
        }


        public void onPrintableChar(char c) {


            // delete currently selected text, if any
            boolean selectionDeleted = false;
            if (isInSelectionMode) {
                selectionDelete();
                selectionDeleted = true;
            }

            int originalLine = cursorLine;

            switch (c) {
                case Language.BACKSPACE:
                    if (selectionDeleted) {
                        break;
                    }

                    if (cursorPosition > 0) {
                        doc.deleteAt(cursorPosition - 1, System.nanoTime());
                        moveCursorLeft(true);

                        if (cursorLine < originalLine) {
                            invalidateFromLine(cursorLine);
                        }
                    }
                    break;

                case Language.NEWLINE:
                    updateLeftPadding();
                    if (isAutoIndent) {
                        char[] indent = createAutoIndent();
                        doc.insertBefore(indent, cursorPosition, System.nanoTime());
                        moveCursor(cursorPosition + indent.length);
                    } else {
                        doc.insertBefore(c, cursorPosition, System.nanoTime());
                        moveCursorRight(true);
                    }

                    invalidateFromLine(originalLine);
                    break;

                default:
                    doc.insertBefore(c, cursorPosition, System.nanoTime());
                    moveCursorRight(true);

                    break;
            }
            refreshSpans();
            if (onEditActionListener != null) {
                onEditActionListener.onUpdateCursor();
            }
        }


        /**
         * Return a char[] with a newline as the 0th element followed by the
         * leading spaces and tabs of the line that the Cursor is on
         */
        private char[] createAutoIndent() {
            int lineNum = doc.findLineNumber(cursorPosition);
            int startOfLine = doc.getLineOffset(lineNum);
            int whitespaceCount = 0;
            doc.seekChar(startOfLine);
            while (doc.hasNext()) {
                char c = doc.next();
                if (c != ' ' && c != Language.TAB) {
                    break;
                }
                ++whitespaceCount;
            }
            char[] indent = new char[1 + whitespaceCount];
            indent[0] = Language.NEWLINE;

            doc.seekChar(startOfLine);
            for (int i = 0; i < whitespaceCount; ++i) {
                indent[1 + i] = doc.next();
            }
            return indent;
        }

        public void moveCursorDown() {
            if (!CursorOnLastLineOfFile()) {
                int currCursor = cursorPosition;
                int currLine = cursorLine;
                int newLine = currLine + 1;
                int currColumn = getColumn(currCursor);
                int currRowLength = doc.getLineSize(currLine);
                int newRowLength = doc.getLineSize(newLine);

                if (currColumn < newRowLength) {
                    cursorPosition += currRowLength;
                } else {
                    cursorPosition +=
                            currRowLength - currColumn + newRowLength - 1;
                }
                ++cursorLine;

                updateSelectionRange(currCursor, cursorPosition);
                if (!makeCharVisible(cursorPosition)) {
                    invalidateLines(currLine, newLine + 1);
                }
                lineChangeListener.onLineChange(newLine);
                stopTextComposing();
            }
        }

        public void moveCursorUp() {
            if (!CursorOnFirstLineOfFile()) {
                int currCursor = cursorPosition;
                int currRow = cursorLine;
                int newRow = currRow - 1;
                int currColumn = getColumn(currCursor);
                int newRowLength = doc.getLineSize(newRow);

                if (currColumn < newRowLength) {
                    cursorPosition -= newRowLength;
                } else {
                    cursorPosition -= (currColumn + 1);
                }
                --cursorLine;

                updateSelectionRange(currCursor, cursorPosition);
                if (!makeCharVisible(cursorPosition)) {
                    invalidateLines(newRow, currRow + 1);
                }
                lineChangeListener.onLineChange(newRow);
                stopTextComposing();
            }
        }

        /**
         * @param isTyping Whether Cursor is moved to a consecutive position as
         *                 a result of entering text
         */
        public void moveCursorRight(boolean isTyping) {
            if (!CursorOnEOF()) {
                int originalRow = cursorLine;
                ++cursorPosition;
                updateCursorLine();
                updateSelectionRange(cursorPosition - 1, cursorPosition);
                if (!makeCharVisible(cursorPosition)) {
                    invalidateLines(originalRow, cursorLine + 1);
                }

                if (!isTyping) {
                    stopTextComposing();
                }
            }
        }

        /**
         * @param isTyping Whether Cursor is moved to a consecutive position as
         *                 a result of deleting text
         */
        public void moveCursorLeft(boolean isTyping) {
            if (cursorPosition > 0) {
                int originalRow = cursorLine;
                --cursorPosition;
                updateCursorLine();
                updateSelectionRange(cursorPosition + 1, cursorPosition);
                if (!makeCharVisible(cursorPosition)) {
                    invalidateLines(cursorLine, originalRow + 1);
                }

                if (!isTyping) {
                    stopTextComposing();
                }
            }
        }

        public void moveCursor(int i) {
            if (i < 0 || i >= doc.docLength()) {
                return;
            }

            updateSelectionRange(cursorPosition, i);
            cursorPosition = i;
            updateAfterCursorJump();
        }

        private void updateAfterCursorJump() {
            int oldRow = cursorLine;
            updateCursorLine();
            if (!makeCharVisible(cursorPosition)) {
                invalidateLines(oldRow, oldRow + 1); //old Cursor row
                invalidateCursorLine(); //new Cursor row
            }
            stopTextComposing();
        }


        /**
         * This helper method should only be used by internal methods after setting
         * _CursorPosition, in order to to recalculate the new row the Cursor is on.
         */
        void updateCursorLine() {
            int newRow = doc.findLineNumber(cursorPosition);
            if (cursorLine != newRow) {
                cursorLine = newRow;
                lineChangeListener.onLineChange(newRow);
            }
        }

        public void stopTextComposing() {
            InputMethodManager im = (InputMethodManager) getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            im.restartInput(EditWidget.this);

            if (inputConnection != null && inputConnection.isComposingStarted()) {
                inputConnection.resetComposingState();
            }
        }

        //- TextFieldController -----------------------------------------------

        /**
         * @return 是否为选择模式
         */
        public final boolean isSelectText() {
            return isInSelectionMode;
        }

        /**
         * 进入或退出选择模式
         *
         * @param mode 是否进入选择模式
         */
        public void setSelectText(boolean mode) {
            if (mode == isInSelectionMode) {
                return;
            }

            if (mode) {
                selectionLeft = cursorPosition;
                selectionRight = cursorPosition;
            } else {
                selectionLeft = -1;
                selectionRight = -1;
            }
            isInSelectionMode = mode;
            selectionModeChangeListener.onSelectionModeChanged(mode);
        }


        /**
         * 字符偏移是否在选择范围内
         * @param charOffset 字符偏移
         * @return 是否在选择范围内
         */
        public boolean inSelectionRange(int charOffset) {
            return isSelectText() && selectionLeft >= 0 && (selectionLeft <= charOffset && charOffset < selectionRight);

        }

        /**
         * Selects numChars count of characters starting from beginPosition.
         * Invalidates necessary areas.
         *
         * @param beginPosition
         * @param numChars
         * @param scrollToStart If true, the start of the selection will be scrolled
         *                      into view. Otherwise, the end of the selection will be scrolled.
         */
        public void setSelectionRange(int beginPosition, int numChars,
                                      boolean scrollToStart) {

            if (beginPosition < 0 || beginPosition > doc.docLength() || beginPosition + numChars > doc.docLength()) {
                return;
            }

            if (isInSelectionMode) {
                invalidateSelectionLines();
            } else {
                invalidateCursorLine();
                setSelectText(true);
            }

            selectionLeft = beginPosition;
            selectionRight = selectionLeft + numChars;

            cursorPosition = selectionRight;
            stopTextComposing();
            updateCursorLine();

            boolean scrolled = makeCharVisible(selectionRight);

            if (scrollToStart) {
                scrolled = makeCharVisible(selectionLeft);
            }


            if (!scrolled) {
                invalidateSelectionLines();
            }
        }

        /**
         * Moves the Cursor to an edge of selected text and scrolls it to view.
         *
         * @param start If true, moves the Cursor to the beginning of
         *              the selection. Otherwise, moves the Cursor to the end of the selection.
         *              In all cases, the Cursor is scrolled to view if it is not visible.
         */
        public void focusSelection(boolean start) {
            if (isInSelectionMode) {
                if (start && cursorPosition != selectionLeft) {
                    cursorPosition = selectionLeft;
                    updateAfterCursorJump();
                } else if (!start && cursorPosition != selectionRight) {
                    cursorPosition = selectionRight;
                    updateAfterCursorJump();
                }
            }
        }


        /**
         * Used by internal methods to update selection boundaries when a new
         * Cursor position is set.
         * Does nothing if not in selection mode.
         */
        private void updateSelectionRange(int oldCursorPosition, int newCursorPosition) {
            if (!isInSelectionMode) {
                return;
            }

            if (oldCursorPosition < selectionRight) {
                if (newCursorPosition > selectionRight) {
                    selectionLeft = selectionRight;
                    selectionRight = newCursorPosition;
                } else {
                    selectionLeft = newCursorPosition;
                }

            } else {
                if (newCursorPosition < selectionLeft) {
                    selectionRight = selectionLeft;
                    selectionLeft = newCursorPosition;
                } else {
                    selectionRight = newCursorPosition;
                }
            }
        }


        //- TextFieldController -----------------------------------------------
        //------------------------ Cut, copy, paste ---------------------------

        /**
         * Convenience method for consecutive copy and paste calls
         */
        public void cut(ClipboardManager cb) {
            copy(cb);
            selectionDelete();
        }

        /**
         * Copies the selected text to the clipboard.
         * <p/>
         * Does nothing if not in select mode.
         */
        public void copy(ClipboardManager cb) {
            //TODO catch OutOfMemoryError
            if (isInSelectionMode &&
                    selectionLeft < selectionRight) {
                char[] contents = doc.subSequence(selectionLeft,
                        selectionRight - selectionLeft);
                cb.setText(new String(contents));
            }
        }

        /**
         * Inserts text at the Cursor position.
         * Existing selected text will be deleted and select mode will end.
         * The deleted area will be invalidated.
         * <p/>
         * After insertion, the inserted area will be invalidated.
         */
        public void paste(String text) {
            if (text == null) {
                return;
            }

            doc.beginBatchEdit();
            selectionDelete();

            int originalRow = cursorLine;
            doc.insertBefore(text.toCharArray(), cursorPosition, System.nanoTime());
            doc.endBatchEdit();

            cursorPosition += text.length();
            updateCursorLine();
            refreshSpans();
            stopTextComposing();

            if (!makeCharVisible(cursorPosition)) {
                //invalidate previous row too if its wrapping changed

                if (originalRow == cursorLine) {
                    //pasted text only affects Cursor row
                    invalidateLines(originalRow, originalRow + 1);
                } else {
                    invalidateFromLine(originalRow);
                }
            }
            updateLeftPadding();
        }

        /**
         * Deletes selected text, exits select mode and invalidates deleted area.
         * If the selected range is empty, this method exits select mode and
         * invalidates the Cursor.
         * <p/>
         * Does nothing if not in select mode.
         */
        public void selectionDelete() {
            if (!isInSelectionMode) {
                return;
            }

            int totalChars = selectionRight - selectionLeft;

            if (totalChars > 0) {
                int originalRow = doc.findLineNumber(selectionLeft);
                boolean isSingleRowSel = doc.findLineNumber(selectionRight) == originalRow;
                doc.deleteAt(selectionLeft, totalChars, System.nanoTime());

                cursorPosition = selectionLeft;
                updateCursorLine();
                refreshSpans();
                setSelectText(false);
                stopTextComposing();

                if (!makeCharVisible(cursorPosition)) {
                    //invalidate previous row too if its wrapping changed

                    if (isSingleRowSel) {
                        //pasted text only affects current row
                        invalidateLines(originalRow, originalRow + 1);
                    } else {
                        //TODO invalidate damaged rows only
                        invalidateFromLine(originalRow);
                    }
                }
            } else {
                setSelectText(false);
                invalidateCursorLine();
            }
        }

        void replaceComposingText(int from, int charCount, String text) {
            int invalidateStartRow;
            boolean isInvalidateSingleRow = true;
            boolean dirty = false;

            //delete selection
            if (isInSelectionMode) {
                invalidateStartRow = doc.findLineNumber(selectionLeft);

                int totalChars = selectionRight - selectionLeft;

                if (totalChars > 0) {
                    cursorPosition = selectionLeft;
                    doc.deleteAt(selectionLeft, totalChars, System.nanoTime());

                    if (invalidateStartRow != cursorLine) {
                        isInvalidateSingleRow = false;
                    }
                    dirty = true;
                }

                setSelectText(false);
            } else {
                invalidateStartRow = cursorLine;
            }

            if (charCount > 0) {
                int delFromRow = doc.findLineNumber(from);
                if (delFromRow < invalidateStartRow) {
                    invalidateStartRow = delFromRow;
                }

                if (invalidateStartRow != cursorLine) {
                    isInvalidateSingleRow = false;
                }


                cursorPosition = from;
                doc.deleteAt(from, charCount, System.nanoTime());
                dirty = true;
            }

            if (text != null && text.length() > 0) {
                int insFromRow = doc.findLineNumber(from);
                if (insFromRow < invalidateStartRow) {
                    invalidateStartRow = insFromRow;
                }

                doc.insertBefore(text.toCharArray(), cursorPosition, System.nanoTime());
                cursorPosition += text.length();
                dirty = true;
            }

            if (dirty) {
                refreshSpans();
            }

            int originalRow = cursorLine;
            updateCursorLine();
            if (originalRow != cursorLine) {
                isInvalidateSingleRow = false;
            }

            if (!makeCharVisible(cursorPosition)) {
                if (isInvalidateSingleRow) {
                    invalidateLines(cursorLine, cursorLine + 1);
                } else {
                    //TODO invalidate damaged rows only
                    invalidateFromLine(invalidateStartRow);
                }
            }
            if (onEditActionListener != null) {
                onEditActionListener.onUpdateCursor();
            }
        }

        void deleteAroundComposingText(int left, int right) {
            int start = cursorPosition - left;
            if (start < 0) {
                start = 0;
            }
            int end = cursorPosition + right;
            int docLength = doc.docLength();
            if (end > (docLength - 1)) {
                end = docLength - 1;
            }
            replaceComposingText(start, end - start, "");
        }

        String getTextAfterCursor(int maxLen) {
            int docLength = doc.docLength();
            if ((cursorPosition + maxLen) > (docLength - 1)) {
                return new String(
                        doc.subSequence(cursorPosition, docLength - cursorPosition - 1));
            }

            return new String(doc.subSequence(cursorPosition, maxLen));
        }

        String getTextBeforeCursor(int maxLen) {
            int start = cursorPosition - maxLen;
            if (start < 0) {
                start = 0;
            }
            return new String(doc.subSequence(start, cursorPosition - start));
        }
    }

    //*********************************************************************
    //************************** InputConnection **************************
    //*********************************************************************
    private class TextFieldInputConnection extends BaseInputConnection {
        private boolean _isComposing = false;
        private int _composingCharCount = 0;

        public TextFieldInputConnection(EditWidget v) {
            super(v, true);
        }

        public void resetComposingState() {
            _composingCharCount = 0;
            _isComposing = false;
            doc.endBatchEdit();
        }

        /**
         * Only true when the InputConnection has not been used by the IME yet.
         * Can be programatically cleared by resetComposingState()
         */
        public boolean isComposingStarted() {
            return _isComposing;
        }

        @Override
        public boolean setComposingText(CharSequence text, int newCursorPosition) {
            _isComposing = true;
            if (!doc.isBatchEdit()) {
                doc.beginBatchEdit();
            }

            editBehaviorController.replaceComposingText(
                    getCursorPosition() - _composingCharCount,
                    _composingCharCount,
                    text.toString());
            _composingCharCount = text.length();

            if (newCursorPosition > 1) {
                editBehaviorController.moveCursor(cursorPosition + newCursorPosition - 1);
            } else if (newCursorPosition <= 0) {
                editBehaviorController.moveCursor(cursorPosition - text.length() - newCursorPosition);
            }
            return true;
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            _isComposing = true;
            editBehaviorController.replaceComposingText(
                    getCursorPosition() - _composingCharCount,
                    _composingCharCount,
                    text.toString());
            _composingCharCount = 0;
            doc.endBatchEdit();

            if (newCursorPosition > 1) {
                editBehaviorController.moveCursor(cursorPosition + newCursorPosition - 1);
            } else if (newCursorPosition <= 0) {
                editBehaviorController.moveCursor(cursorPosition - text.length() - newCursorPosition);
            }
            if (onAutoCompletionListener != null) {
                onAutoCompletionListener.onPopCodeComplete(text);

            }
            return true;
        }


        @Override
        public boolean deleteSurroundingText(int leftLength, int rightLength) {

            editBehaviorController.deleteAroundComposingText(leftLength, rightLength);
            return true;
        }

        @Override
        public boolean finishComposingText() {
            resetComposingState();
            return true;
        }

        @Override
        public int getCursorCapsMode(int reqModes) {
            int capsMode = 0;
            if ((reqModes & InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                    == InputType.TYPE_TEXT_FLAG_CAP_WORDS) {
                int prevChar = cursorPosition - 1;
                if (prevChar < 0 ||language.isWhitespace(doc.charAt(prevChar))) {
                    capsMode |= InputType.TYPE_TEXT_FLAG_CAP_WORDS;

                    if ((reqModes & InputType.TYPE_TEXT_FLAG_CAP_SENTENCES)
                            == InputType.TYPE_TEXT_FLAG_CAP_SENTENCES) {
                        capsMode |= InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
                    }
                }
            } else {
                Language lang = language;

                int prevChar = cursorPosition - 1;
                int whitespaceCount = 0;
                boolean capsOn = true;
                while (prevChar >= 0) {
                    char c = doc.charAt(prevChar);
                    if (c == Language.NEWLINE) {
                        break;
                    }

                    if (!lang.isWhitespace(c)) {
                        if (whitespaceCount == 0 || !lang.isSentenceTerminator(c)) {
                            capsOn = false;
                        }
                        break;
                    }

                    ++whitespaceCount;
                    --prevChar;
                }

                if (capsOn) {
                    capsMode |= InputType.TYPE_TEXT_FLAG_CAP_SENTENCES;
                }
            }

            return capsMode;
        }

        @Override
        public CharSequence getTextAfterCursor(int maxLen, int flags) {
            return editBehaviorController.getTextAfterCursor(maxLen);
        }


        @Override
        public CharSequence getTextBeforeCursor(int maxLen, int flags) {
            return editBehaviorController.getTextBeforeCursor(maxLen);
        }

        @Override
        public boolean setSelection(int start, int end) {
            if (start == end) {
                editBehaviorController.moveCursor(start);
            } else {
                editBehaviorController.setSelectionRange(start, end - start, false);
            }
            return true;
        }

    }


}
