package be.witspirit.rfidtrialprocess;

import java.io.File;
import java.util.function.Predicate;

/**
 * The processor will listen for new files in an input directory and pick them up
 * and create transformed TOS Instruction files in an output directory
 */
public class RfidProcessor {
    public RfidProcessor(File inputDir, File outputDir) {

    }

    public void handle(Predicate<String> fileNameFilter, TosTransformer tosTransformer) {

    }

    public void listenForEvents() {

    }
}
