package org.twpp.text.complete;

import org.twpp.text.IEditor;

public interface IAutoCompleteFactory {

    IAutoCompleteController create(IEditor editor);

}
