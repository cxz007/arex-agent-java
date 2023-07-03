package io.arex.inst.runtime.context;

import io.arex.agent.bootstrap.util.ConcurrentHashSet;
import io.arex.agent.bootstrap.util.StringUtil;
import io.arex.inst.runtime.model.ArexConstants;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ArexContext {

    private final String caseId;
    private final String replayId;
    private final long createTime;
    private final SequenceProvider sequence;
    private final Set<Integer> methodSignatureHashList = new ConcurrentHashSet<>();
    private final Map<String, Object> cachedReplayResultMap = new ConcurrentHashMap<>();
    private Map<String, Set<String>> excludeMockTemplate;

    private Map<String, Object> attachments = null;

    private boolean isRedirectRequest;

    public static ArexContext of(String caseId) {
        return of(caseId, null);
    }

    public static ArexContext of(String caseId, String replayId) {
        return new ArexContext(caseId, replayId);
    }

    private ArexContext(String caseId, String replayId) {
        this.createTime = System.currentTimeMillis();
        this.caseId = caseId;
        this.sequence = new SequenceProvider();
        this.replayId = replayId;
    }

    public String getCaseId() {
        return this.caseId;
    }

    public String getReplayId() {
        return this.replayId;
    }

    public long getCreateTime() {
        return createTime;
    }

    public boolean isRecord() {
        return !isReplay();
    }

    public boolean isReplay() {
        return StringUtil.isNotEmpty(this.replayId);
    }

    public int calculateSequence(String target) {
        return StringUtil.isEmpty(target) ? 0 : sequence.get(target);
    }

    public Set<Integer> getMethodSignatureHashList() {
        return methodSignatureHashList;
    }

    public Map<String, Object> getCachedReplayResultMap() {
        return cachedReplayResultMap;
    }
    public Map<String, Set<String>> getExcludeMockTemplate() {
        return excludeMockTemplate;
    }

    public void setExcludeMockTemplate(Map<String, Set<String>> excludeMockTemplate) {
        this.excludeMockTemplate = excludeMockTemplate;
    }

    public void setAttachment(String key, Object value) {
        if (attachments == null) {
            attachments = new HashMap<>();
        }

        attachments.put(key, value);
    }

    public Object getAttachment(String key) {
        if (attachments == null) {
            return null;
        }

        return attachments.get(key);
    }

    public boolean isRedirectRequest() {
        return isRedirectRequest;
    }

    public void setRedirectRequest(boolean redirectRequest) {
        isRedirectRequest = redirectRequest;
    }

    public boolean isRedirectRequest(String referer) {
        if (attachments == null) {
            isRedirectRequest = false;
            return false;
        }

        if (StringUtil.isEmpty(referer)) {
            isRedirectRequest = false;
            return false;
        }

        Object location = attachments.get(ArexConstants.REDIRECT_REFERER);

        if (location == null) {
            isRedirectRequest = false;
            return false;
        }
        isRedirectRequest = Objects.equals(location, referer);
        return isRedirectRequest;
    }

    public void clear() {
        methodSignatureHashList.clear();
        cachedReplayResultMap.clear();
        sequence.clear();
        if (excludeMockTemplate != null) {
            excludeMockTemplate.clear();
        }
        if (attachments != null) {
            attachments.clear();
        }
    }
}
