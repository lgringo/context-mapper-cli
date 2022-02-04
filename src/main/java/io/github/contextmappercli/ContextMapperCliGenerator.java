package io.github.contextmappercli;

import guru.nidi.graphviz.engine.Format;
import org.contextmapper.dsl.cml.CMLResource;
import org.contextmapper.dsl.generator.AbstractContextMapGenerator;
import org.contextmapper.dsl.generator.contextmap.ContextMapFormat;
import org.contextmapper.dsl.generator.contextmap.ContextMapModelConverter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.xtext.generator.IFileSystemAccess2;

import java.io.IOException;

import static org.contextmapper.dsl.generator.contextmap.ContextMapFormat.DOT;
import static org.contextmapper.dsl.generator.contextmap.ContextMapFormat.SVG;

public class ContextMapperCliGenerator extends AbstractContextMapGenerator {
    private String file;
    private ContextMapFormat format = ContextMapFormat.SVG;
    private int labelSpacingFactor = 5;

    private int width = -1;
    private int height = -1;
    private boolean useWidth = true;
    private boolean printAdditionalLabels = false;
    private boolean clusterTeams = true;

    public ContextMapperCliGenerator() {
    }

    public CMLResource loadCml() {
        return new CMLResource(new ResourceSetImpl().getResource(URI.createFileURI(file), true));
    }

    @Override
    protected void generateFromContextMap(final org.contextmapper.dsl.contextMappingDSL.ContextMap cmlContextMap, final IFileSystemAccess2 fsa, final URI inputFileURI) {
        org.contextmapper.contextmap.generator.model.ContextMap contextMap = new ContextMapModelConverter().convert(cmlContextMap, printAdditionalLabels);
        org.contextmapper.contextmap.generator.ContextMapGenerator generator = createContextMapGenerator();
        generator.setLabelSpacingFactor(labelSpacingFactor);
        generator.clusterTeams(clusterTeams);
        if (this.width > 0 && useWidth) {
            generator.setWidth(width);
        } else if (this.height > 0) {
            generator.setHeight(height);
        }

        try {
            OutputStreamFsa ofsa = (OutputStreamFsa)fsa;
            generator.generateContextMapGraphic(contextMap, getGraphvizLibFormat(format), ofsa.getOutputStream(inputFileURI));
        } catch (IOException e) {
            throw new RuntimeException("An error occured while generating the Context Map!", e);
        }
    }

    protected org.contextmapper.contextmap.generator.ContextMapGenerator createContextMapGenerator() {
        return new org.contextmapper.contextmap.generator.ContextMapGenerator();
    }

    private Format getGraphvizLibFormat(final ContextMapFormat format) {
        if (format == SVG)
            return Format.SVG;
        if (format == DOT)
            return Format.DOT;
        return Format.PNG;
    }

    public String getFile() {
        return file;
    }

    public void setFile(final String file) {
        this.file = file;
    }

    public ContextMapFormat getFormat() {
        return format;
    }

    public void setFormat(final ContextMapFormat format) {
        this.format = format;
    }

    public int getLabelSpacingFactor() {
        return labelSpacingFactor;
    }

    public void setLabelSpacingFactor(final int labelSpacingFactor) {
        this.labelSpacingFactor = labelSpacingFactor;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(final int height) {
        this.height = height;
    }

    public boolean isUseWidth() {
        return useWidth;
    }

    public void setUseWidth(final boolean useWidth) {
        this.useWidth = useWidth;
    }

    public boolean isPrintAdditionalLabels() {
        return printAdditionalLabels;
    }

    public void setPrintAdditionalLabels(final boolean printAdditionalLabels) {
        this.printAdditionalLabels = printAdditionalLabels;
    }

    public boolean isClusterTeams() {
        return clusterTeams;
    }

    public void setClusterTeams(final boolean clusterTeams) {
        this.clusterTeams = clusterTeams;
    }
}
