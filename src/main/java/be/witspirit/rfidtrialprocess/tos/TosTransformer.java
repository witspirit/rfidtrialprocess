package be.witspirit.rfidtrialprocess.tos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Converts Scans to TosInstructions
 */
public class TosTransformer {
    private static final Logger LOG = LoggerFactory.getLogger(TosTransformer.class);

    private String tosPattern;

    public TosTransformer(String tosPattern) {
        this.tosPattern = tosPattern;
    }

    public <SCAN> List<TosInstruction> scansToTos(List<SCAN> scans, Function<SCAN, String> vinExtractor) {
        return vinsToTos(scans.stream().map(vinExtractor));
    }

    public List<TosInstruction> vinsToTos(List<String> vins) {
        return vinsToTos(vins.stream());
    }

    public List<TosInstruction> vinsToTos(Stream<String> vins) {
        return vins.map(vin -> new TosInstruction(tosPattern, vin)).collect(Collectors.toList());
    }

}
