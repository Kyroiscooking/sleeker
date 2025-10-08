/*
 * Copyright (c) 2025. This project is fully authored by Thiago Rigonatti (https://github.com/thiagorigonatti)
 * and is available under Apache License Version 2.0, January 2004 http://www.apache.org/licenses/
 */

package me.thiagorigonatti.sleeker.util;

public class AsciiArt {

    public static final String SLEEKER_LOGO = """
            
            &0    &3__&b___&2__&a__.&e__                 &b__          &fv0.0.9   \033[0m
            &0   &3/   &b__&2__&a_/|  &e|   &5__&d__   &3__&b__ |  &a| _&e_ &c__&4__&5__&d__&9__&b_&0   \033[0m
            &0   &b\\&3_&b__&2__  &a\\ &e|  | &5_/ &d__ &9\\&3_/ &b__ \\|  &a|/ &e/&c/ &4__ &5\\&d_  &9_&b_ \\  \033[0m
            &b   /        &e\\|  &c|_&5\\  &d_&9__&3/&b\\  ___/&a|    &e<&c\\  &4_&5__&d/|  &b| \\/  \033[0m
            &b  /__&2__&a__&e_  /|&4__&5__/&d\\_&9__  &b>\\_&2__  &a>__&e|_ \\&c\\_&5__  &9>__&b|     \033[0m
            &e             Authored by &9Thiago &bRigonatti             \033[0m
            """
            .replace("&0", ColorCode.c0)
            .replace("&1", ColorCode.c1)
            .replace("&2", ColorCode.c2)
            .replace("&3", ColorCode.c3)
            .replace("&4", ColorCode.c4)
            .replace("&5", ColorCode.c5)
            .replace("&6", ColorCode.c6)
            .replace("&7", ColorCode.c7)
            .replace("&8", ColorCode.c8)
            .replace("&9", ColorCode.c9)
            .replace("&a", ColorCode.ca)
            .replace("&b", ColorCode.cb)
            .replace("&c", ColorCode.cc)
            .replace("&d", ColorCode.cd)
            .replace("&e", ColorCode.ce)
            .replace("&f", ColorCode.cf);
}
