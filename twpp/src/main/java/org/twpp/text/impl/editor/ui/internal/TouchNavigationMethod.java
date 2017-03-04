package org.twpp.text.impl.editor.ui.internal;

import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.ActionMode;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.Toast;

import org.twpp.text.R;
import org.twpp.text.impl.editor.common.DocumentProvider;
import org.twpp.text.impl.editor.ui.SelectionModeChangeListener;
import org.twpp.text.skin.Skin;

//import android.support.v4.view.MotionEventCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.view.ActionMode;


/**
 * 编辑器的手势控制模块
 */
public class TouchNavigationMethod extends GestureDetector.SimpleOnGestureListener implements ActionMode.Callback {

    private static final int CURSOR_HIDE_DELAYED = 3000;
    /**
     * 光标对应的矩形
     */
    private Rect cursorBloat = new Rect(0, 0, 0, 0);

    /**
     * 手指误差范围
     */
    protected static int TOUCH_SLOP = 12;
    /**
     * 编辑器引用
     */
    protected EditWidget editWidget;
    /**
     * 光标是否被触摸
     */
    protected boolean _isCursorTouched = false;
    /**
     * 手势识别
     */
    private GestureDetector _gestureDetector;
    private ActionMode actionMode;
    /**
     * 用于双指缩放
     */
    private double lastDist;

    //=========================================
    //             光标控制器
    //=========================================
    protected static int SCROLL_EDGE_SLOP = 10;
    protected boolean isNearHandle = false;
    protected boolean isNearHandleStart = false;
    protected boolean isNearHandleEnd = false;
    private Paint handlePaint = new Paint();

    private int restShowHandleTime = 0;
    private boolean needToDrawHandle = false;

    private int sideHandleWidth;
    private int sideHandleHeight;


    private int midHandleWidth;
    private int midHandleHeight;

    private Bitmap leftHandleBitmap = null;
    private Bitmap midHandleBitmap = null;
    private Bitmap rightHandleBitmap = null;

    public TouchNavigationMethod(EditWidget textField) {
        editWidget = textField;
        editWidget.setSelModeListener(new SelectionModeChangeListener() {
            @Override
            public void onSelectionModeChanged(boolean active) {
                if (active) {
//                    IContextManager contextManager = ServiceManager.get().getService(IContextManager.class);
//                    actionMode = ((AppCompatActivity)contextManager.getCurrentActivity())
//                            .startSupportActionMode(TouchNavigationMethod.this);
                } else {
                    if (actionMode != null) {
                        actionMode.finish();
                        actionMode = null;
                    }
                }
            }
        });
        _gestureDetector = new GestureDetector(textField.getContext(), this);
        _gestureDetector.setIsLongpressEnabled(true);
    }

    @Override
    public void onLongPress(final MotionEvent e) {
        int coordToCharIndex = this.editWidget.coordToCharIndex(screenToViewX((int) e.getX()), screenToViewY((int) e.getY()));
        if (!this.isNearHandle) {
            if (!this.editWidget.inSelectionRange(coordToCharIndex)) {
                if (coordToCharIndex >= 0) {
                    char charAt;
                    this.editWidget.moveCursor(coordToCharIndex);
                    DocumentProvider createDocumentProvider = this.editWidget.getDoc();
                    int i = coordToCharIndex;
                    while (i >= 0) {
                        charAt = createDocumentProvider.charAt(i);
                        if ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && ((charAt < '0' || charAt > '9') && charAt != '_'))) {
                            break;
                        }
                        i--;
                    }
                    if (i != coordToCharIndex) {
                        i++;
                    }
                    while (coordToCharIndex >= 0) {
                        charAt = createDocumentProvider.charAt(coordToCharIndex);
                        if ((charAt < 'a' || charAt > 'z') && ((charAt < 'A' || charAt > 'Z') && ((charAt < '0' || charAt > '9') && charAt != '_'))) {
                            break;
                        }
                        coordToCharIndex++;
                    }
                    this.editWidget.setSelectionRange(i, coordToCharIndex - i);
                    this.editWidget.selectText(true);
                    setNeedToDrawHandle(true);
                }

            }
            super.onLongPress(e);
        }
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        int x = screenToViewX((int) motionEvent.getX());
        int y = screenToViewY((int) motionEvent.getY());
        this._isCursorTouched = isNearChar(x, y, this.editWidget.getCursorPosition());
        this.isNearHandle = isNearHandle(x, y, this.editWidget.getCursorPosition()) && !this._isCursorTouched && this.restShowHandleTime > 0 && !this.editWidget.isSelectText();
        this.isNearHandleStart = false;
        this.isNearHandleEnd = false;
        if (this.editWidget.isFlingScrolling()) {
            this.editWidget.stopFlingScrolling();
        } else if (this.editWidget.isSelectText()) {
            if (isNearChar(x, y, this.editWidget.getSelectionStart())) {
                this.editWidget.focusSelectionStart();
                this.editWidget.performHapticFeedback(0);
                this._isCursorTouched = true;
            } else if (isNearChar(x, y, this.editWidget.getSelectionEnd())) {
                this.editWidget.focusSelectionEnd();
                this.editWidget.performHapticFeedback(0);
                this._isCursorTouched = true;
            } else if (isNearHandle(x, y, this.editWidget.getSelectionStart(), 1)) {
                this.editWidget.focusSelectionStart();
                this.isNearHandleStart = true;
            } else if (isNearHandle(x, y, this.editWidget.getSelectionEnd(), 0)) {
                this.editWidget.focusSelectionEnd();
                this.isNearHandleEnd = true;
            }
        }
        if (this._isCursorTouched) {
            this.editWidget.performHapticFeedback(0);
        }
        return true;
    }

    public boolean onUp(MotionEvent e) {
        _isCursorTouched = false;
        this.isNearHandle = false;
        return true;
    }

    public boolean isNearHandle(int x, int y, int charOffset) {
        Rect bounds = this.editWidget.getBoundingBox(charOffset);
        return y >= bounds.top + this.editWidget.getFontHeight() && y < (bounds.top + this.editWidget.getFontHeight()) + this.midHandleHeight && x >= bounds.left - (this.midHandleWidth / 2) && x < bounds.left + (this.midHandleWidth / 2);
    }

    public boolean isNearHandle(int x, int y, int charOffset, int unknownYet) {
        Rect bounds = this.editWidget.getBoundingBox(charOffset);
        return y >= bounds.top + this.editWidget.getFontHeight() && y < (bounds.top + this.editWidget.getFontHeight()) + this.sideHandleHeight && x >= bounds.left - (this.sideHandleWidth * unknownYet) && x < bounds.left + (this.sideHandleWidth * (1 - unknownYet));
    }

    public void drawMidHandle(Canvas canvas) {
        if (this.midHandleBitmap == null) {
            this.midHandleWidth = this.editWidget.getAdvance('M') * 4;
            if (this.midHandleBitmap == null) {
                this.midHandleBitmap = BitmapFactory.decodeResource(this.editWidget.getResources(), R.drawable.text_select_handle_middle);
            }
            this.midHandleHeight = (this.midHandleWidth * this.midHandleBitmap.getHeight()) / this.midHandleBitmap.getWidth();
            cursorBloat = new Rect(0, 0, 0, this.midHandleHeight);
            this.midHandleBitmap = Bitmap.createScaledBitmap(this.midHandleBitmap, this.midHandleWidth, this.midHandleHeight, true);
        }
        Rect bounds = this.editWidget.getBoundingBox(this.editWidget.getCursorPosition());
        canvas.drawBitmap(this.midHandleBitmap, (float) ((bounds.left + this.editWidget.getPaddingLeft()) - (this.midHandleWidth / 2)), (float) (bounds.top + this.editWidget.getFontHeight()), this.handlePaint);
    }


    public void drawDoubleHandle(Canvas canvas) {
        if (this.leftHandleBitmap == null) {
            this.sideHandleWidth = this.editWidget.getAdvance('M') * 4;
            if (this.leftHandleBitmap == null) {
                this.leftHandleBitmap = BitmapFactory.decodeResource(this.editWidget.getResources(), R.drawable.text_select_handle_left);
            }
            this.sideHandleHeight = (this.sideHandleWidth * this.leftHandleBitmap.getHeight()) / this.leftHandleBitmap.getWidth();
            this.leftHandleBitmap = Bitmap.createScaledBitmap(this.leftHandleBitmap, this.sideHandleWidth, this.sideHandleHeight, true);
            if (this.rightHandleBitmap == null) {
                this.rightHandleBitmap = BitmapFactory.decodeResource(this.editWidget.getResources(), R.drawable.text_select_handle_right);
            }
            this.rightHandleBitmap = Bitmap.createScaledBitmap(this.rightHandleBitmap, this.sideHandleWidth, this.sideHandleHeight, true);
        }
        Rect bounds = this.editWidget.getBoundingBox(this.editWidget.getSelectionStart());
        canvas.drawBitmap(this.leftHandleBitmap, (float) ((bounds.left + this.editWidget.getLineNumberPadding()) - this.sideHandleWidth * 3 / 4), (float) (bounds.top + this.editWidget.getFontHeight()), this.handlePaint);
        bounds = this.editWidget.getBoundingBox(this.editWidget.getSelectionEnd());
        canvas.drawBitmap(this.rightHandleBitmap, (float) ((bounds.left + this.editWidget.getLineNumberPadding()) - this.sideHandleWidth / 4), (float) (bounds.top + this.editWidget.getFontHeight()), this.handlePaint);
    }


    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
        float Xdist = Math.abs(e1.getRawX() - e2.getRawX());
        float Ydist = Math.abs(e1.getRawY() - e2.getRawY());

        if (Xdist > Ydist) {
            distanceY = 0;
        } else {
            distanceX = 0;
        }
        if (this._isCursorTouched || this.isNearHandle || this.isNearHandleStart || this.isNearHandleEnd) {
            dragCursor(e2);
        } else {
            scrollView(distanceX, distanceY);
        }

        if ((e2.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
            onUp(e2);
        }
        return true;
    }

    private void dragCursor(MotionEvent motionEvent) {
        boolean z = false;
        int x = (int) motionEvent.getX();
        int y = (int) motionEvent.getY();
        if (this.isNearHandle) {
            y -= this.editWidget.getFontHeight() + (this.midHandleHeight / 2);
        }
        if (this.isNearHandleStart || this.isNearHandleEnd) {
            y -= this.editWidget.getFontHeight() + (this.sideHandleHeight / 2);
            x = this.isNearHandleStart ? x + (this.sideHandleWidth / 4) : x - (this.sideHandleWidth / 4);
        }
        if (this.isNearHandle || this.isNearHandleStart || this.isNearHandleEnd) {
            x += (this.editWidget.getAdvance('M') + this.editWidget.getAdvance('.')) / 4;
        }
        int paddingLeft = (x - this.editWidget.getPaddingLeft()) + this.editWidget.getLineNumberPadding();
        int paddingTop = y - this.editWidget.getPaddingTop();
        if (paddingLeft < SCROLL_EDGE_SLOP) {
            z = this.editWidget.autoScrollCursor(EditWidget.ScrollTarget.SCROLL_LEFT);
        } else if (paddingLeft >= (this.editWidget.getContentWidth() + this.editWidget.getLineNumberPadding()) - SCROLL_EDGE_SLOP) {
            z = this.editWidget.autoScrollCursor(EditWidget.ScrollTarget.SCROLL_RIGHT);
        } else if (paddingTop < SCROLL_EDGE_SLOP) {
            z = this.editWidget.autoScrollCursor(EditWidget.ScrollTarget.SCROLL_UP);
        } else if (paddingTop >= this.editWidget.getContentHeight() - SCROLL_EDGE_SLOP) {
            z = this.editWidget.autoScrollCursor(EditWidget.ScrollTarget.SCROLL_DOWN);
        }
        if (!z) {
            y = this.editWidget.coordToCharIndex(screenToViewX(x), screenToViewY(y));
            if (y >= 0) {
                this.editWidget.moveCursor(y);
            }
        }
        if (this.restShowHandleTime > 0) {
            setRestShowHandleTime(CURSOR_HIDE_DELAYED);
        }
        if (this.needToDrawHandle) {
            this.editWidget.invalidate();
        }
    }


    private void scrollView(float distanceX, float distanceY) {
        int newX = (int) distanceX + editWidget.getScrollX();
        int newY = (int) distanceY + editWidget.getScrollY();

        int maxWidth = Math.max(editWidget.getMaxScrollX(),
                editWidget.getScrollX());
        if (newX > maxWidth) {
            newX = maxWidth;
        } else if (newX < 0) {
            newX = 0;
        }

        int maxHeight = Math.max(editWidget.getMaxScrollY(),
                editWidget.getScrollY());
        if (newY > maxHeight) {
            newY = maxHeight;
        } else if (newY < 0) {
            newY = 0;
        }
        editWidget.scrollTo(newX, newY);
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent motionEvent) {
        int x = screenToViewX((int) motionEvent.getX());
        int y = screenToViewY((int) motionEvent.getY());
        int coordToCharIndex = this.editWidget.coordToCharIndex(x, y);
        if (this.editWidget.isSelectText()) {
            int coordToCharIndexStrict = this.editWidget.coordToCharIndexStrict(x, y);
            if (!(this.editWidget.inSelectionRange(coordToCharIndexStrict) || isNearHandle(x, y, this.editWidget.getSelectionStart(), 1) || isNearHandle(x, y, this.editWidget.getSelectionEnd(), 0))) {
                this.editWidget.selectText(false);
                if (coordToCharIndexStrict >= 0) {
                    this.editWidget.moveCursor(coordToCharIndex);
                }
            } else {
                editWidget.showIME(true);
            }
        } else {
            if (coordToCharIndex >= 0) {
                this.editWidget.moveCursor(coordToCharIndex);
            }
            this.editWidget.showIME(true);
        }
        if (!this.editWidget.isSelectText()) {
            performShowHandleTask();
        }
        return true;
    }

    private void performShowHandleTask() {
        if (this.restShowHandleTime == 0) {
            this.editWidget.postDelayed(new Runnable() {
                @Override
                public void run() {
                    restShowHandleTime -= 100;
                    if (restShowHandleTime == 0) {
                        editWidget.invalidate();
                    } else {
                        editWidget.postDelayed(this, 100);
                    }
                }
            }, 100);
        }
        this.editWidget.post(new Runnable() {

            @Override
            public void run() {
                setRestShowHandleTime(CURSOR_HIDE_DELAYED);
            }
        });
    }

    @Override
    public boolean onDoubleTap(MotionEvent motionEvent) {
        this._isCursorTouched = true;
        int charOffset = this.editWidget.coordToCharIndex(screenToViewX((int) motionEvent.getX()), screenToViewY((int) motionEvent.getY()));
        if (charOffset >= 0) {
            if (editWidget.isSelectText()) {
                if (editWidget.inSelectionRange(charOffset)) {
                    return true;
                }
            }
            editWidget.moveCursor(charOffset);
            editWidget.selectText(false);
            performShowHandleTask();
        }
        return true;
    }


    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        if (!_isCursorTouched || e.getAction() != MotionEvent.ACTION_MOVE) {
            return super.onDoubleTapEvent(e);
        }
        dragCursor(e);
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        float xDist = Math.abs(e1.getRawX() - e2.getRawX());
        float yDist = Math.abs(e1.getRawY() - e2.getRawY());

        //单向滑动,要么上下滑动,要么左右滑动
        if (xDist > yDist) {
            velocityY = 0;
        } else {
            velocityX = 0;
        }

        if (!(this._isCursorTouched || this.isNearHandle || this.isNearHandleStart || this.isNearHandleEnd)) {
            editWidget.flingScroll((int) (-velocityX), (int) (-velocityY));
        }
        onUp(e2);
        return true;
    }


    public void setRestShowHandleTime(int restTime) {
        this.restShowHandleTime = restTime;
        this.editWidget.invalidate();
    }

    public void setNeedToDrawHandle(boolean needToDrawHandle) {
        this.needToDrawHandle = needToDrawHandle;
        this.editWidget.invalidate();
    }


    /**
     * 触摸事件
     *
     * @param motionEvent 事件对象
     */
    public boolean onTouchEvent(MotionEvent motionEvent) {

        onTouchZoom(motionEvent);
        boolean onTouchEvent = this._gestureDetector.onTouchEvent(motionEvent);

        return (onTouchEvent || (motionEvent.getAction() & MotionEvent.ACTION_MASK) != MotionEvent.ACTION_UP) ? onTouchEvent : onUp(motionEvent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return false;
    }

    public void onTextDrawComplete(Canvas canvas) {
        if (this.needToDrawHandle) {
            if (this.editWidget.isSelectText() && editWidget.getSelectionStart() != editWidget.getSelectionEnd()) {
                drawDoubleHandle(canvas);
            } else {
                setNeedToDrawHandle(false);
            }
        } else if (this.restShowHandleTime > 0) {
            drawMidHandle(canvas);
        }
    }

    /**
     * 主题切换时回调
     *
     * @param colorScheme 新的主题
     */
    public void onColorSchemeChanged(Skin colorScheme) {

    }

    /**
     * 取得光标对应的矩形
     *
     * @return 光标对应的矩形
     */
    public Rect getCursorBloat() {
        return cursorBloat;
    }

    final protected int getPointerId(MotionEvent e) {
        return (e.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
    }

    /**
     * Converts a x-coordinate from screen coordinates to local coordinates,
     * excluding padding
     */
    final protected int screenToViewX(int x) {
        return x - editWidget.getPaddingLeft() + editWidget.getScrollX();
    }


    final protected int screenToViewY(int y) {
        return y - editWidget.getPaddingTop() + editWidget.getScrollY();
    }

    private boolean isDragSelect() {
        return false;
    }

    public boolean isNearChar(int x, int y, int charOffset) {
        Rect bounds = editWidget.getBoundingBox(charOffset);

        return (y >= (bounds.top - TOUCH_SLOP)
                && y < (bounds.bottom + TOUCH_SLOP)
                && x >= (bounds.left - TOUCH_SLOP)
                && x < (bounds.right + TOUCH_SLOP)
        );
    }


    public boolean onTouchZoom(MotionEvent motionEvent) {

        if (motionEvent.getAction() != 2) {
            this.lastDist = 0;
        } else if (motionEvent.getPointerCount() == 2) {//当前有俩手指
            double spacing = getDistance(motionEvent);
            if (this.lastDist != 0) {
                this.editWidget.setTextSize((float) (this.editWidget.getTextSize() * (spacing / this.lastDist)));
            }
            this.lastDist = spacing;
        }
        return false;
    }


    /**
     * 得到两根手指之间的距离
     *
     * @param event 触摸事件
     * @return
     */
    private static float getDistance(MotionEvent event) {
        float a = event.getX(1) - event.getX(0);
        float b = event.getY(1) - event.getY(0);
        return (float) Math.sqrt(Math.pow(a, 2) + Math.pow(b, 2));
    }


    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//        IEventManager eventManager = ServiceManager.get().getService(IEventManager.class);
        menu.add("paste").setIcon(R.drawable.abc_ic_menu_paste_mtrl_am_alpha).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        menu.add("select all").setIcon(R.drawable.abc_ic_menu_selectall_mtrl_alpha).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        if (editWidget.isSelectText()) {
            menu.add("copy").setIcon(R.drawable.abc_ic_menu_copy_mtrl_am_alpha).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            menu.add("cut").setIcon(R.drawable.abc_ic_menu_cut_mtrl_alpha).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        boolean finish = true;
        ClipboardManager manager = (ClipboardManager) editWidget.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        if (item.getTitle().equals("paste")) {
            editWidget.paste(manager.getText().toString());
//            IEventManager eventManager = ServiceManager.get().getService(IEventManager.class);
//            if (eventManager != null) {
//                eventManager.sendEvent(new SyncModifyEvent(true));
//            }

        } else if (item.getTitle().equals("copy")) {
            editWidget.copy(manager);
            Toast.makeText(editWidget.getContext(), "Copy success!", Toast.LENGTH_SHORT).show();
        } else if (item.getTitle().equals("cut")) {
            editWidget.cut(manager);
        } else if (item.getTitle().equals("select all")) {
            editWidget.selectAll();
            finish = false;
        }
        if (finish) {
            editWidget.selectText(false);
        }
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        this.actionMode = null;
    }


}
