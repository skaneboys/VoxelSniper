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

package com.thevoxelbox.voxelsniper.util;

public class SimpleMap<K,V> {
    private SimpleEntry[] entries = new SimpleEntry[1000];
    private int openIndex = 0;
    
    public void put(K k, V v) {
        SimpleEntry entry = new SimpleEntry(k, v);
        
        if (!containsKey(k)) {
            openIndex++;
            entries[openIndex] = entry;
        } else {
            entries[getIndexOfKey(k)] = entry;
        }
    }
    
    public void removeKey(K k) {
        if (containsKey(k)) {
            entries[getIndexOfKey(k)] = null;
            shuffleEntries(getIndexOfKey(k));
            openIndex--;
        }
    }
    
    public boolean containsKey(K k) {
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].getKey().equals(k))
                return true;
            
            if (entries[i + 1] == null)
                break;
        }
        
        return false;
    }
    
    private void shuffleEntries(int index) {
        for (int i = index; i < entries.length; i++) {
            entries[i] = entries[i + 1];
            
            if (entries[i] == null)
                break;
        }
    }
    
    private int getIndexOfKey(K k) {
        for (int i = 0; i < entries.length; i++) {
            if (entries[i].getKey().equals(k))
                return i;
            
            if (entries[i + 1] == null)
                break;
        }
        
        return -1;
    }
}
