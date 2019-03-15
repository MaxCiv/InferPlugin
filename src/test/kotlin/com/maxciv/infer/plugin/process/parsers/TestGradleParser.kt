package com.maxciv.infer.plugin.process.parsers

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/**
 * @author maxim.oleynik
 * @since 14.03.2019
 */
class TestGradleParser {

    @Test
    fun getCompilerArgs() {
        val result = GradleParser.getCompilerArgs(LOGS.lines())
        result.forEach { System.out.println(it) }
        assertEquals(RESULT_LIST, result)
    }

    companion object {
        val RESULT_LIST = listOf(
            "-source",
            "1.8",
            "-target",
            "1.8",
            "-d",
            "/Users/maxim.oleynik/projects/testInfer/build/classes/java/main",
            "-g",
            "-sourcepath",
            " ",
            "-proc:none",
            "-XDuseUnsharedTable=true",
            "-classpath",
            "/Users/maxim.oleynik/.gradle/caches/modules-2/files-2.1/com.google.code.gson/gson/2.8.5/f645ed69d595b24d4cf8b3fbb64cc505bede8829/gson-2.8.5.jar:/Users/maxim.oleynik/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-databind/2.9.8/11283f21cc480aa86c4df7a0a3243ec508372ed2/jackson-databind-2.9.8.jar:/Users/maxim.oleynik/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-core/2.9.8/f5a654e4675769c716e5b387830d19b501ca191/jackson-core-2.9.8.jar:/Users/maxim.oleynik/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-annotations/2.9.0/7c10d545325e3a6e72e06381afe469fd40eb701/jackson-annotations-2.9.0.jar"
        )
        const val LOGS = """
[52836][environment] CWD = /Users/maxim.oleynik/projects/testInfer
[52836][environment] No .inferconfig file found
[52836][environment] Project root = /Users/maxim.oleynik/projects/testInfer
[52836][environment] INFER_ARGS =   @/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args.tmp.d7c2f7
[52836][environment]   ++Contents of '/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args.tmp.d7c2f7':
[52836][environment]     capture
[52836][environment]
[52836][environment] command line arguments:   /Users/maxim.oleynik/study/infer-osx-v0.15.0/bin/infer
[52836][environment]                           capture -- ./gradlew -d buildAnalyzer: capture
[52836][environment] Active checkers: annotation reachability (Java), biabduction (C/C++/ObjC, Java), fragment retains view (Java), immutable cast (Java), liveness (C/C++/ObjC), printf args (Java), ownership (C/C++/ObjC), RacerD (C/C++/ObjC, Java), SIOF (C/C++/ObjC), uninitialized variables (C/C++/ObjC)
[52836][environment] Infer log identifier is
Infer version v0.15.0
[52836][environment] Copyright 2009 - present Facebook. All Rights Reserved.
[52836][environment]
[52836][   progress] Capturing in gradle mode...

[52870][environment] CWD = /Users/maxim.oleynik/projects/testInfer
[52870][environment] No .inferconfig file found
[52870][environment] Project root = /Users/maxim.oleynik/projects/testInfer
[52870][environment] INFER_ARGS =   @/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args.tmp.5f4747
[52870][environment]   ++Contents of '/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args.tmp.5f4747':
[52870][environment]     @/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args.tmp.d7c2f7 capture
[52870][environment]     --continue
[52870][environment]     ++Contents of '/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args.tmp.d7c2f7':
[52870][environment]       capture
[52870][environment]
[52870][environment]
[52870][environment] command line arguments:   /Users/maxim.oleynik/study/infer-osx-v0.15.0/lib/infer/infer/lib/python/inferlib/../../../bin/infer
[52870][environment]                           capture --continue -- javac -source 1.8 -target 1.8
[52870][environment]                           -d
[52870][environment]                           /Users/maxim.oleynik/projects/testInfer/build/classes/java/main
[52870][environment]                           -g -sourcepath  -proc:none -XDuseUnsharedTable=true
[52870][environment]                           -classpath

[52870][environment]                           /Users/maxim.oleynik/.gradle/caches/modules-2/files-2.1/com.google.code.gson/gson/2.8.5/f645ed69d595b24d4cf8b3fbb64cc505bede8829/gson-2.8.5.jar:/Users/maxim.oleynik/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-databind/2.9.8/11283f21cc480aa86c4df7a0a3243ec508372ed2/jackson-databind-2.9.8.jar:/Users/maxim.oleynik/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-core/2.9.8/f5a654e4675769c716e5b387830d19b501ca191/jackson-core-2.9.8.jar:/Users/maxim.oleynik/.gradle/caches/modules-2/files-2.1/com.fasterxml.jackson.core/jackson-annotations/2.9.0/7c10d545325e3a6e72e06381afe469fd40eb701/jackson-annotations-2.9.0.jar
[52870][environment]                           @/Users/maxim.oleynik/projects/testInfer/infer-out/filelists/gradle_mG4swk.txt
[52870][environment]   ++Contents of '/Users/maxim.oleynik/projects/testInfer/infer-out/filelists/gradle_mG4swk.txt':
[52870][environment]     /Users/maxim.oleynik/projects/testInfer/src/main/java/infertest/Pointers.java
[52870][environment]     /Users/maxim.oleynik/projects/testInfer/src/main/java/infertest/model/Car.java
[52870][environment]     /Users/maxim.oleynik/projects/testInfer/src/main/java/infertest/Resources.java
[52870][environment]     /Users/maxim.oleynik/projects/testInfer/src/main/java/infertest/Hello.java
[52870][environment]     /Users/maxim.oleynik/projects/testInfer/src/main/java/infertest/test/Pointers222.java
[52870][environment]
[52870][environment] Analyzer: capture
[52870][environment] Active checkers: annotation reachability (Java), biabduction (C/C++/ObjC, Java), fragment retains view (Java), immutable cast (Java), liveness (C/C++/ObjC), printf args (Java), ownership (C/C++/ObjC), RacerD (C/C++/ObjC, Java), SIOF (C/C++/ObjC), uninitialized variables (C/C++/ObjC)
[52870][      debug] Current working directory: '/Users/maxim.oleynik/projects/testInfer'
[52870][      debug] Trying to execute: javac '-J-Duser.language=en' '@/Users/maxim.oleynik/projects/testInfer/infer-out/filelists/gradle_mG4swk.txt' '@/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/args_.tmp.a0323b' 2>'/var/folders/qm/b52k4bns2_g798x_j9t4sflc0000gp/T/javac.tmp.51851a.out'
[52870][      debug] *** Success. Logs:
[52870][      debug] Translating 5 source files (7 classes)
[52870][      debug] done capturing all files
[52870][environment] Infer log identifier is
        """
    }
}
