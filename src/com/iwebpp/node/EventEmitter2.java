// Copyright (c) 2014 Tom Zhou<iwebpp@gmail.com>


package com.iwebpp.node;

import java.nio.ByteBuffer;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import android.util.Log;


public class EventEmitter2 
implements EventEmitter {
	private final static String TAG = "EventEmitter2";
    private Map<String, List<Listener>> events;
    private Map<String, Integer> maxEvents;
    
    protected EventEmitter2() {
    	this.events = new Hashtable<String, List<Listener>>();
    	this.maxEvents = new Hashtable<String, Integer>();
    }
    
	@Override
	public boolean emit(final String event) throws Exception {
		if (events.containsKey(event)) {
			Log.d(TAG, "emit "+event+" at="+this);
			for (Listener cb : events.get(event))
				cb.onEvent(null);
		} else {
			Log.d(TAG, "unknown event "+event+" at="+this);
			return false;
		}
		return true;
	}

	@Override
	public boolean emit(final String event, final Object data) throws Exception {
		if (events.containsKey(event)) {
			Log.d(TAG, "emit "+event+" data="+data+" at="+this);

			for (Listener cb : events.get(event))
				// always create new one to share in case ByteBuffer, etc
				if (data instanceof ByteBuffer) {
					ByteBuffer bb = ((ByteBuffer)data).slice();
					cb.onEvent(bb);
				} else 
					cb.onEvent(data);
		} else {
			Log.d(TAG, "unknown event "+event+" data "+data+" at="+this);
			return false;
		}
		return true;
	}
		
	@Override
	public EventEmitter addListener(final String event, final Listener cb) {
		// check maxListens
		if (maxEvents.containsKey(event) && 
			maxEvents.get(event) < listenerCount(event)) {
			Log.w(TAG, "exceed maxListeners@"+event+" at="+this);

			///return this;
		}
		
		if (!events.containsKey(event)) {
			events.put(event, new LinkedList<Listener>());
		}
		
		Log.d(TAG, "addListener "+event+" cb="+cb+" at="+this);

		events.get(event).add(cb);
		
		return this;
	}
	
	@Override
	public EventEmitter addListener(final String event, final Listener cb, final int priority) {
		// check maxListens
		if (maxEvents.containsKey(event) && 
				maxEvents.get(event) < listenerCount(event)) {
			Log.w(TAG, "exceed maxListeners@"+event+" at="+this);

			///return this;
		}

		if (!events.containsKey(event)) {
			events.put(event, new LinkedList<Listener>());
		}
		
		if (priority < listenerCount(event))
			events.get(event).add(priority, cb);
		else 
			events.get(event).add(cb);
		
		return this;
	}

	@Override
	public EventEmitter on(final String event, final Listener cb) throws Exception {
		return addListener(event, cb);
	}

	@Override
	public EventEmitter once(final String event, final Listener ocb) {
		return addListener(event, new Listener(){

			@Override
			public void onEvent(final Object data) throws Exception {
				// TODO Auto-generated method stub
				ocb.onEvent(data);

				// remove listener
				removeListener(event, this);
			}

		});
	}

	@Override
	public EventEmitter removeListener(final String event, final Listener cb) {
		if (events.containsKey(event) && events.get(event).contains(cb))
			events.get(event).remove(cb);

		return this;
	}

	@Override
	public EventEmitter removeListener(final String event) {
		if (events.containsKey(event))
			events.remove(event);
		
		return this;
	}

	@Override
	public EventEmitter removeListener() {
		// TODO Auto-generated method stub
		events.clear();
		return this;
	}

	@Override
	public EventEmitter removeAllListeners() {
		// TODO Auto-generated method stub
		events.clear();
		return this;
	}
	
	@Override
	public EventEmitter setMaxListeners(final String event, final int n) {
		// TODO Auto-generated method stub
		this.maxEvents.put(event, n);
		return this;
	}

	@Override
	public List<Listener> listeners(final String event) {
		// TODO Auto-generated method stub
		return events.containsKey(event) ? events.get(event) : null;
	}

	@Override
	public int listenerCount(final String event) {
		// TODO Auto-generated method stub
		return events.containsKey(event) ? events.get(event).size() : 0;
	}

}
