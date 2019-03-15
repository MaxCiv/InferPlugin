package com.maxciv.infer.plugin.process.parsers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author maxim.oleynik
 * @since 11.03.2019
 */
class TestMavenParser {

    @Test
    fun getCompilerArgs() {
        val result = MavenParser.getCompilerArgs(LOGS.lines())
        result.forEach { System.out.println(it) }
        assertEquals(RESULT_LIST, result)
    }

    companion object {
        val RESULT_LIST = listOf(
            "-d",
            "/Users/maxim.oleynik/projects/infertest/target/classes",
            "-classpath",
            "/Users/maxim.oleynik/projects/infertest/target/classes",
            "-sourcepath",
            "/Users/maxim.oleynik/projects/infertest 01/src/main/java:/Users/maxim.oleynik/projects/infertest/target/generated-sources/annotations",
            "-s",
            "/Users/maxim.oleynik/projects/infertest/target/generated-sources/annotations",
            "-g",
            "-nowarn",
            "-target",
            "1.8",
            "-source",
            "1.8"
        )
        const val LOGS = """
[52030][environment] CWD = /Users/maxim.oleynik/projects/infertest
[52030][environment] No .inferconfig file found
[52030][environment] Project root = /Users/maxim.oleynik/projects/infertest
[52030][environment] INFER_ARGS =   @/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args.tmp.38d7ca
[52030][environment]   ++Contents of '/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args.tmp.38d7ca':
[52030][environment]     capture
[52030][environment]
[52030][environment] command line arguments:   /Users/maxim.oleynik/study/infer-osx-v0.15.0/bin/infer
[52030][environment]                           capture -- mvn -X compileAnalyzer: capture
[52030][environment] Active checkers: annotation reachability (Java), biabduction (C/C++/ObjC, Java), fragment retains view (Java), immutable cast (Java), liveness (C/C++/ObjC), printf args (Java), ownership (C/C++/ObjC), RacerD (C/C++/ObjC, Java), SIOF (C/C++/ObjC), uninitialized variables (C/C++/ObjC)
[52030][environment] Infer log identifier is
Infer version v0.15.0
[52030][environment] Copyright 2009 - present Facebook. All Rights Reserved.
[52030][environment]
[52030][   progress] Capturing in maven mode...
[52030][      debug] Running maven capture:
[52030][      debug] mvn '-X' 'compile' '-P' 'infer-capture'

[52053][environment] CWD = /Users/maxim.oleynik/projects/infertest
[52053][environment] No .inferconfig file found
[52053][environment] Project root = /Users/maxim.oleynik/projects/infertest
[52053][environment] INFER_ARGS =   @/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args.tmp.4ae2b5
[52053][environment]   ++Contents of '/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args.tmp.4ae2b5':
[52053][environment]     @/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args.tmp.38d7ca
[52053][environment]     @/Users/maxim.oleynik/projects/infertest/target/classes/org.codehaus.plexus.compiler.javac.JavacCompiler9093682279698246199arguments
[52053][environment]     ++Contents of '/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args.tmp.38d7ca':
[52053][environment]       capture
[52053][environment]     ++Contents of '/Users/maxim.oleynik/projects/infertest/target/classes/org.codehaus.plexus.compiler.javac.JavacCompiler9093682279698246199arguments':
[52053][environment]       "-d" "/Users/maxim.oleynik/projects/infertest/target/classes"
[52053][environment]       "-classpath" "/Users/maxim.oleynik/projects/infertest/target/classes;"
[52053][environment]       "-sourcepath"
[52053][environment]       "/Users/maxim.oleynik/projects/infertest 01/src/main/java:/Users/maxim.oleynik/projects/infertest/target/generated-sources/annotations:"
[52053][environment]       "/Users/maxim.oleynik/projects/infertest 01/src/main/java/com/maxciv/infertest/Resources.java"
[52053][environment]       "/Users/maxim.oleynik/projects/infertest 01/src/main/java/com/maxciv/infertest/Hello.java"
[52053][environment]       "/Users/maxim.oleynik/projects/infertest 01/src/main/java/com/maxciv/infertest/test/Pointers222.java"
[52053][environment]       "/Users/maxim.oleynik/projects/infertest 01/src/main/java/com/maxciv/infertest/Pointers.java"
[52053][environment]       "-s"
[52053][environment]       "/Users/maxim.oleynik/projects/infertest/target/generated-sources/annotations"
[52053][environment]       "-g" "-nowarn" "-target" "1.8" "-source" "1.8"
[52053][environment]
[52053][environment]
[52053][environment] command line arguments:   /Users/maxim.oleynik/study/infer-osx-v0.15.0/lib/infer/infer/bin/infer
[52053][environment]                           @/Users/maxim.oleynik/projects/infertest/target/classes/org.codehaus.plexus.compiler.javac.JavacCompiler9093682279698246199arguments
[52053][environment]   ++Contents of '/Users/maxim.oleynik/projects/infertest/target/classes/org.codehaus.plexus.compiler.javac.JavacCompiler9093682279698246199arguments':
[52053][environment]     "-d" "/Users/maxim.oleynik/projects/infertest/target/classes"
[52053][environment]     "-classpath" "/Users/maxim.oleynik/projects/infertest/target/classes:"
[52053][environment]     "-sourcepath"
[52053][environment]     "/Users/maxim.oleynik/projects/infertest/src/main/java:/Users/maxim.oleynik/projects/infertest/target/generated-sources/annotations:"
[52053][environment]     "/Users/maxim.oleynik/projects/infertest/src/main/java/com/maxciv/infertest/Resources.java"
[52053][environment]     "/Users/maxim.oleynik/projects/infertest/src/main/java/com/maxciv/infertest/Hello.java"
[52053][environment]     "/Users/maxim.oleynik/projects/infertest/src/main/java/com/maxciv/infertest/test/Pointers222.java"
[52053][environment]     "/Users/maxim.oleynik/projects/infertest/src/main/java/com/maxciv/infertest/Pointers.java"
[52053][environment]     "-s"
[52053][environment]     "/Users/maxim.oleynik/projects/infertest/target/generated-sources/annotations"
[52053][environment]     "-g" "-nowarn" "-target" "1.8" "-source" "1.8"
[52053][environment]
[52053][environment] Analyzer: capture
[52053][environment] Active checkers: annotation reachability (Java), biabduction (C/C++/ObjC, Java), fragment retains view (Java), immutable cast (Java), liveness (C/C++/ObjC), printf args (Java), ownership (C/C++/ObjC), RacerD (C/C++/ObjC, Java), SIOF (C/C++/ObjC), uninitialized variables (C/C++/ObjC)
[52053][      debug] Current working directory: '/Users/maxim.oleynik/projects/infertest'
[52053][      debug] Trying to execute: javac '-J-Duser.language=en' '@/Users/maxim.oleynik/projects/infertest/target/classes/org.codehaus.plexus.compiler.javac.JavacCompiler9093682279698246199arguments' '@/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args_.tmp.c02af4' 2>'/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/javac.tmp.186ee8.out'
[52053][      debug] *** Success. Logs:
[52053][      debug] Translating 4 source files (6 classes)
[52053][      debug] done capturing all files
[52053][environment] Infer log identifier is
        """
    }
}
