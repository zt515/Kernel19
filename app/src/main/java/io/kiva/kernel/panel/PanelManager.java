package io.kiva.kernel.panel;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;

/**
 * @author kiva
 */

public class PanelManager {
    private ViewGroup.LayoutParams layoutParams;
    private HashMap<String, Panel> panels;
    private ViewGroup panelContainer;
    private Context context;

    public PanelManager(ViewGroup panelContainer) {
        this.panels = new HashMap<>(5);
        this.panelContainer = panelContainer;
        this.context = panelContainer.getContext();
        layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    public void addPanel(String key, Panel panel) {
        panels.put(key, panel);
    }

    public void removePanel(String key, Panel panel) {
        panels.remove(key);
    }

    public Panel getPanel(String key) {
        return panels.containsKey(key) ? panels.get(key) : null;
    }

    public void switchToPanel(String key) {
        Panel panel = getPanel(key);
        panelContainer.removeAllViews();

        if (panel != null) {
            View content = panel.getView(context);
            panelContainer.addView(content, layoutParams);
        }
    }

    public void show() {
        panelContainer.setVisibility(View.VISIBLE);
    }

    public void dismiss() {
        panelContainer.setVisibility(View.GONE);
    }

    public boolean isShowing() {
        return panelContainer.getVisibility() == View.VISIBLE;
    }
}
