/*
 * Copyright (C) 2011 - 2012, psanker and contributors
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are 
 * permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright notice, this list of 
 *   conditions and the following 
 * * Redistributions in binary form must reproduce the above copyright notice, this list of 
 *   conditions and the following disclaimer in the documentation and/or other materials 
 *   provided with the distribution.
 * * Neither the name of The VoxelPlugineering Team nor the names of its contributors may be 
 *   used to endorse or promote products derived from this software without specific prior 
 *   written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS 
 * OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF 
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE 
 * COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR 
 * TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.thevoxelbox.voxelsniper.brush;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public class BrushManager {
    protected HashMap<String, Class<? extends Brush>> brushAliases = new HashMap<String, Class<? extends Brush>>();
    
    public void loadBrushes(Package pack) throws BrushException {
        loadBrushes(pack.getName());
    }
    
    public void loadBrushes(String packagePath) throws BrushException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        
        if (classLoader != null) {
            String path = packagePath.replace(".", "/");
            try {
                Enumeration<URL> resources = classLoader.getResources(path);
                List<File> dirs = new ArrayList<File>();
                
                while (resources.hasMoreElements()) {
                    URL resource = resources.nextElement();
                    dirs.add(new File(resource.getFile()));
                }
                
                ArrayList<Class> classes = new ArrayList<Class>();
                
                for (File dir : dirs) {
                    try {
                        classes.addAll(findClasses(dir, packagePath));
                    } catch (ClassNotFoundException ex) {
                        continue;
                    }
                }
                
                // Cut out extraneous classes
                Class[] clazzes = classes.toArray(new Class[classes.size()]);
                Class[] toRemove = new Class[classes.size()];
                int i = 0;
                
                for (Class clazz : clazzes) {
                    if (!(clazz.isAssignableFrom(Brush.class))) {
                        toRemove[i] = clazz;
                        i++;
                    }
                }
                
                if (toRemove.length > 0) {
                    for (Class clazz : toRemove)
                        classes.remove(clazz);
                }
                
                Class[] cleaned = classes.toArray(new Class[classes.size()]);
                
                for (Class cls : cleaned) {
                    loadBrush(cls);
                }
                
            } catch (IOException ex) {
                throw new BrushException(ex.getMessage());
            }
        } else {
            throw new BrushException("Null class loader");
        }
    }
    
    public void loadBrushes(Class<? extends Brush>[] classes) {
        for (Class cls : classes) {
            try {
                loadBrush(cls);
            } catch (BrushException ex) {
                continue;
            }
        }
    }
    
    public void loadBrush(Class<? extends Brush> clazz) throws BrushException {
        assert clazz.isAssignableFrom(Brush.class);
        
        if (!clazz.isAnnotationPresent(MetaData.class))
            throw new BrushException("Incorrectly registered brush");
        
        MetaData md = clazz.getAnnotation(MetaData.class);
        String[] aliases = md.aliases();
        
        for (String alias : aliases) {
            if (!brushAliases.containsKey(alias))
                brushAliases.put(alias, clazz);
        }
    }
    
    private List<Class> findClasses(File dir, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        
        if (!dir.exists())
            return classes;
        
        File[] files = dir.listFiles();
        
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        
        return classes;
    }
    
    public Class<? extends Brush> getBrushForAlias(String alias) throws BrushNotFoundException {
        if (!brushAliases.containsKey(alias))
            throw new BrushNotFoundException("Brush not found: " + alias);
        
        return brushAliases.get(alias);
    }
}
