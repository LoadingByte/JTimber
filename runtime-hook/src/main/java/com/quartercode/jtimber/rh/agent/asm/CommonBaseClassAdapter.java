/*
 * This file is part of JTimber.
 * Copyright (c) 2015 QuarterCode <http://quartercode.com/>
 *
 * JTimber is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JTimber is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JTimber. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.jtimber.rh.agent.asm;

import static org.objectweb.asm.Opcodes.ASM5;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Type;

/**
 * A common base class for all ASM {@link ClassVisitor}s used by the runtime hook.
 */
class CommonBaseClassAdapter extends ClassVisitor {

    protected Type classType;

    /**
     * Creates a new common base class adapter.
     * 
     * @param cv The class visitor to which this visitor delegates method calls. May be {@code null}.
     */
    protected CommonBaseClassAdapter(ClassVisitor cv) {

        super(ASM5, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {

        classType = Type.getObjectType(name);

        super.visit(version, access, name, signature, superName, interfaces);
    }

}
