/***********************************************************************************************************************
 * 
 * BetterBeansBinding - keeping JavaBeans in sync
 * ==============================================
 * 
 * Copyright (C) 2009 by Tidalwave s.a.s. (http://www.tidalwave.it)
 * http://betterbeansbinding.kenai.com
 * 
 * This is derived work from BeansBinding: http://beansbinding.dev.java.net
 * BeansBinding is copyrighted (C) by Sun Microsystems, Inc.
 * 
 ***********************************************************************************************************************
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General 
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option) 
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied 
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more 
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to 
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 ***********************************************************************************************************************
 * 
 * $Id: AstFunction.java 34 2009-04-25 17:27:10Z fabriziogiudici $
 * 
 **********************************************************************************************************************/
/*
 * Copyright (C) 2007 Sun Microsystems, Inc. All rights reserved. Use is
 * subject to license terms.
 *
 *//* Generated By:JJTree: Do not edit this line. AstFunction.java */

package org.jdesktop.el.impl.parser;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jdesktop.el.ELContext;
import org.jdesktop.el.ELException;
import org.jdesktop.el.FunctionMapper;

import org.jdesktop.el.impl.lang.EvaluationContext;
import org.jdesktop.el.impl.util.MessageFactory;

/**
 * @author Jacob Hookom [jacob@hookom.net]
 * @version $Change: 181177 $$DateTime: 2001/06/26 08:45:09 $$Author: fabriziogiudici $
 */
public final class AstFunction extends SimpleNode {

    protected String localName = "";

    protected String prefix = "";

    public AstFunction(int id) {
        super(id);
    }

    public String getLocalName() {
        return localName;
    }

    public String getOutputName() {
        if (this.prefix == null) {
            return this.localName;
        } else {
            return this.prefix + ":" + this.localName;
        }
    }

    public String getPrefix() {
        return prefix;
    }

    public Class getType(EvaluationContext ctx)
            throws ELException {
        
        FunctionMapper fnMapper = ctx.getFunctionMapper();
        
        // quickly validate again for this request
        if (fnMapper == null) {
            throw new ELException(MessageFactory.get("error.fnMapper.null"));
        }
        Method m = fnMapper.resolveFunction(this.prefix, this.localName);
        if (m == null) {
            throw new ELException(MessageFactory.get("error.fnMapper.method",
                    this.getOutputName()));
        }
        return m.getReturnType();
    }

    public Object getValue(EvaluationContext ctx)
            throws ELException {
        
        FunctionMapper fnMapper = ctx.getFunctionMapper();
        
        // quickly validate again for this request
        if (fnMapper == null) {
            throw new ELException(MessageFactory.get("error.fnMapper.null"));
        }
        Method m = fnMapper.resolveFunction(this.prefix, this.localName);
        if (m == null) {
            throw new ELException(MessageFactory.get("error.fnMapper.method",
                    this.getOutputName()));
        }

        Class[] paramTypes = m.getParameterTypes();
        Object[] params = null;
        Object result = null;
        int numParams = this.jjtGetNumChildren();
        if (numParams > 0) {
            params = new Object[numParams];
            try {
                for (int i = 0; i < numParams; i++) {
                    params[i] = this.children[i].getValue(ctx);
                    if (params[i] == ELContext.UNRESOLVABLE_RESULT) {
                        return ELContext.UNRESOLVABLE_RESULT;
                    }
                    params[i] = coerceToType(params[i], paramTypes[i]);
                }
            } catch (ELException ele) {
                throw new ELException(MessageFactory.get("error.function", this
                        .getOutputName()), ele);
            }
        }
        try {
            result = m.invoke(null, params);
        } catch (IllegalAccessException iae) {
            throw new ELException(MessageFactory.get("error.function", this
                    .getOutputName()), iae);
        } catch (InvocationTargetException ite) {
            throw new ELException(MessageFactory.get("error.function", this
                    .getOutputName()), ite.getCause());
        }
        return result;
    }

    public void setLocalName(String localName) {
        this.localName = localName;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
    
    
    public String toString()
    {
        return ELParserTreeConstants.jjtNodeName[id] + "[" + this.getOutputName() + "]";
    }
}
