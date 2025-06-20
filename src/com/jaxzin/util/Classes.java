package com.jaxzin.util;

import java.util.*;
import java.util.jar.*;
import java.io.*;

/** This is a static utility class used to get a list of all classes in classpath.
 * This class is not meant to be instantiated and only includes static methods.
 * It gives a user access to the classes accessible via the classpath and
 * groups them by their packages.
 *
 * @author Brian Jackson, (c) 1999-2001
 * @version 2001-08-05
 */
public final class Classes 
{
    /** This holds Lists of String filenames 
     * keyed to the package they are in.
     */
    private static Map packages = new HashMap();

    /** This is the static initializer and is run
     * when the class Classes is loaded into the JVM
     */
    static  
    {
        refresh();
    }
    
    /** Classes only contains static methods and is not
     * able to be instantiated
     */
    private Classes() {}
    
    /** The public method allows a programmer to force a
     * refresh of the Classes memory
     */
    public static void refresh() 
    {
        // Release the old version
        packages = new HashMap();
        // Get the classpaths
        String classpath = System.getProperty("java.class.path");
        //System.out.println("java.class.path="+classpath);
        // Separate the paths into tokens
        StringTokenizer paths = new StringTokenizer(classpath, File.pathSeparator);

        // Create a Vector to store references to each jar file in the path
        Vector jarfiles = new Vector();
        String path;
        Enumeration files;
        JarFile jarfile;
        Manifest man;
        Map entries;
        JarEntry file;
        byte[] bytes;
        Class jarclass;
        boolean found = false;
        // For each path in classpath...
        while (paths.hasMoreTokens())
        {
            path = paths.nextToken();
            // Discover if path is to a jar file
            try 
            {
                jarfile = new JarFile(path);
                // Add it to the jar file Vector
                jarfiles.add(jarfile);
                found = true;
            } catch (FileNotFoundException e) 
            {
                jarfile = null;
                found = false;
            } catch (IOException e) 
            {	// This will be thrown if directory
                jarfile = null;
                found = false;
            }
            
            if (found) // This is a jar or zip file
            {
                // For each entry(file, dir, etc...)...
                for(files = jarfile.entries(); files.hasMoreElements();) 
                {
                    file = (JarEntry) files.nextElement();
                    // If the entry is not a directory...
                    if (!file.isDirectory())
                    {
                        // Reconstruct name
                        String packname = "";
                        String name = "";
                        String temp = "";
                        // Jar entries for class files will have a 
                        // format like "package/subpack/My.class"
                        // First strip the ".class" from name
                        StringTokenizer s1	= new StringTokenizer(file.getName(), ".");
                        // Split packages into tokens (should use JarFile.pathSeparator or similar)
                        StringTokenizer s	= new StringTokenizer(s1.nextToken(), "/");

                        // Check that this entry is a class file(ends in '.class'
                        if (s1.hasMoreTokens() && s1.nextToken().equals("class")) 
                        {
                            // If entry has slashes, add all but the
                            // last token to the package name and make the
                            // last token the class name.  
                            while(s.hasMoreTokens()) 
                            {
                                temp = s.nextToken();
                                // If this is not the last token...
                                if (s.hasMoreTokens()) 
                                {
                                    // Add the token to the package name
                                    if (!packname.equals("")) packname += ".";
                                    packname = packname + temp;
                                } else
                                // else this is the last token
                                {   // The last token is the class name
                                    name = temp;
                                }
                            }
                        }
                        
                        
                        
                        // Get the list of classes for this package(if there is a name)
                        if(!"".equals(name)) {
                            List l = (List) packages.get(packname);
                            // If the list doesn't exist, create it
                            if(l == null)
                                packages.put(packname, l = new ArrayList());
                            // Add the class to the package list
                            l.add(name);
                            //System.out.println(packname + "." + name);
                        }
                    }
                }
            } else  // This is a directory
            {
                // Get the File object for this directory
                File dir = new File(path);
                // Search this directory for more classes
                recursive_search("", dir);
            }
        }

    } // END: static (this is the static initializer)

    /** This method is responsible for adding all the class files in the
     * directory 'dir' to the member Map 'packages' keyed to the package of
     * 'curpack'.  It will also call itself on any directories it finds in
     * 'dir', also appending the name of that directory to 'curpack'.
     */
    private static void recursive_search(String curpack, File dir) 
    {
        // Get a list of all items in cur directory
        String[] files = dir.list();

        File cur; // Used to instantiate a file
        String new_package_name;

        // Iterate through every item in current directory
        for(int i = 0; i < files.length; i++) 
        {
            // Get a pointer to current item
            cur = new File(dir.getAbsolutePath() + File.separator + files[i]);
            // If cur item is a directory...
            if (cur.isDirectory()) 
            {	// Figure out what the new package name is and...
                if(!curpack.equals("")) new_package_name = curpack + "." + files[i];
                else new_package_name = files[i];	
                // Search the new directory
                recursive_search(new_package_name, cur);
            } else // if cur item is a file
            {
                // Figure out if this file has an extension of "class"
                StringTokenizer f = new StringTokenizer(files[i], ".");
                // Valid class files can't have '.' in name so it must
                // separate extention if there is a '.'
                String class_name = f.nextToken();
                String ext = "";

                if (f.hasMoreTokens()) 
                    ext = f.nextToken();

                if (ext.equals("class")) 
                {
                    // Retrieve the List of classes in this
                    // package from the static Map 'packages'
                    List l = (List) packages.get(curpack);
                    // If a list of classes for this package
                    // doesn't exist, create it
                    if(l == null)
                        packages.put(curpack, l = new ArrayList());
                    // Add this class to this package
                    l.add(class_name);	

                    /*if(!curpack.equals("")) System.out.println(curpack + "." + class_name);
                    else System.out.println(class_name);*/
                    
                } // END: if (ext.equals("class"))
            } // END: if (cur.isDirectory())...else
        } // END: for(int i = 0; i < files.length; i++) 
    } // END: private static void recursive_search(String curpack, File dir) 



    /** This method returns a List of class names contained in
     * the specified package.  If the package does not exist in the current
     * class path this method returns null.
     * @param packageName name of package to list
     * @return null if specified package does not exist 
 */
    public static List getByPackage(String packageName) 
    {
        // If there is no package by this name return null
        if(packages.get(packageName) == null) return null;

        // Work on a copy of the class list
        List l = null;
        synchronized(packages) {
        l = (List) ((ArrayList) packages.get(packageName)).clone();
        }

        // Make sure all of these actually are of this package
        for(int i = 0; i < l.size(); i++) 
        {
            try 
            {
                // Load the class and make sure its package matches the package name
                Package p = Class.forName((String) l.get(i)).getPackage();
                String pname;

                if(p != null) pname = p.getName();
                else pname = "";		

                if (!pname.equals(packageName))
                {
                    l.remove(i);
                    i--;
                }
            } catch (ClassNotFoundException ignored) 
            {
            }
        }

        return l;
    }
    
    /** This method returns the names of all the packages found in 
     * the class path.  This is similar to the java.lang.Package
     * method getPackages()
     */
    public static String[] getPackageNames() {
        // Can't just type cast because it was never
        // a String[] to begin with, it was always
        // an Object[]
        Object keys[] = packages.keySet().toArray();
        String keyStrs[] = new String[keys.length];
        for(int i=0;i<keys.length;i++) {
            keyStrs[i] = (String) keys[i];
        }
        return keyStrs;
    }
    
    /** This main tests this class and accepts a package name as an
     * argument.  If a package name is specified, all classes found in that
     * package will be printed.  If no name is specifed, all packages and
     * all classes are output.
     */
    public static void main(String[] args) 
    {
        if(args.length == 1) {
            System.out.println("Printing classes found in current classpath under package '"+args[0]+"'");
            List l = Classes.getByPackage(args[0]);
            if(l != null) {
                for(Iterator j = l.iterator();j.hasNext();) {
                    String c = (String) j.next();
                    System.out.println("   "+c);
                }
            } else {
                System.out.println("Package '"+args[0]+"' does not exist in classpath");
            }
        } else {
            System.out.println("Usage: java Classes package_name");
            String[] packNames = Classes.getPackageNames();
            for(int i=0; i < packNames.length; i++) {
                System.out.println(packNames[i]);
                List l = Classes.getByPackage(packNames[i]);
                if(l != null) {
                    for(Iterator j = l.iterator();j.hasNext();) {
                        String c = (String) j.next();
                        System.out.println("   "+c);
                    }
                } else {
                    System.out.println("Package '"+packNames[i]+"' does not exist in classpath");
                }
            }
        }
    }
}
