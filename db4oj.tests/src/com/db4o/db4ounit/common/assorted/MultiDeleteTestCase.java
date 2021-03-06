/* Copyright (C) 2004 - 2005  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.config.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class MultiDeleteTestCase extends AbstractDb4oTestCase implements OptOutDefragSolo {
	
	public static void main(String[] args) {
		new MultiDeleteTestCase().runSoloAndClientServer();
	}
	
	public static class Item {
	    public Item child;
	    public String name;
	    public Object forLong;
	    public Long myLong;
	    public Object[] untypedArr;
	    public Long[] typedArr;
	    
	    public void setMembers() {
	        forLong = new Long(100);
	        myLong = new Long(100);
	        untypedArr = new Object[]{
	            new Long(10),
	            "hi",
	            new Item()
	        };
	        typedArr = new Long[]{
	            new Long(3),
	            new Long(7),
	            new Long(9),
	        };
	    }
	}
	
	protected void configure(Configuration config) {
		ObjectClass itemClass = config.objectClass(Item.class);
		itemClass.cascadeOnDelete(true);
        itemClass.cascadeOnUpdate(true);
	}
	
	protected void store() throws Exception {
        Item md = new Item();
        md.name = "killmefirst";
        md.setMembers();
        md.child = new Item();
        md.child.setMembers();
        db().store(md);
    }
    
    public void testDeleteCanBeCalledTwice(){
        Item item = itemByName("killmefirst");
        Assert.isNotNull(item);
        long id = db().getID(item);
        db().delete(item);
        
        Assert.areSame(item, itemById(id));
        
        db().delete(item);
        Assert.areSame(item, itemById(id));
    }

	private Item itemByName(String name) {
		Query q = newQuery(Item.class);
        q.descend("name").constrain(name);
		return (Item)q.execute().next();
	}

	private Item itemById(long id) {
		return (Item)db().getByID(id);
	}
}
