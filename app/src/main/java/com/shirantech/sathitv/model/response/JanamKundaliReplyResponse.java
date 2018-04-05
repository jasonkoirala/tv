package com.shirantech.sathitv.model.response;

import com.google.gson.annotations.SerializedName;
import com.shirantech.sathitv.model.JanamKundali;

import java.util.List;

/**
 * Class for holding response of Janam kundali reply.
 */
public class JanamKundaliReplyResponse extends GeneralResponse {
    @SerializedName("rashifals")
    private List<JanamKundali> janamKundaliList;

    public List<JanamKundali> getJanamKundaliList() {
        return janamKundaliList;
    }
}
