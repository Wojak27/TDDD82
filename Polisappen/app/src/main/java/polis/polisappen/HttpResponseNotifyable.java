package polis.polisappen;

import java.util.HashMap;
import java.util.Map;

public interface HttpResponseNotifyable {

    void notifyAboutResponse(HashMap<String,String> response);
    void notifyAboutResponseJSONArray(HashMap<String,HashMap<String, String>> response);
}
