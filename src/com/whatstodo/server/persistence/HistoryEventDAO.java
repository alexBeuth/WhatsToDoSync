package com.whatstodo.server.persistence;

import java.util.List;

import com.whatstodo.models.HistoryEvent;
import com.whatstodo.models.HistoryEvent.Action;
import com.whatstodo.models.HistoryEvent.Type;

public interface HistoryEventDAO extends BaseDAO<HistoryEvent> {

	List<HistoryEvent> find(Type type, Long entityUid, Long parentEntityUid, Action action,
			Long after, Boolean isSynchronized);

}