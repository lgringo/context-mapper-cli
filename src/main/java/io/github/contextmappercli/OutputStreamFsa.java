package io.github.contextmappercli;

import org.eclipse.emf.common.util.URI;
import org.eclipse.xtext.generator.IFileSystemAccess2;
import org.eclipse.xtext.util.RuntimeIOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class OutputStreamFsa implements IFileSystemAccess2 {

    private Map<URI, OutputStream> store = new HashMap<>();

    @Override
    public boolean isFile(final String path, final String outputConfigurationName) throws RuntimeIOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isFile(final String path) throws RuntimeIOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void generateFile(final String fileName, final CharSequence contents) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void generateFile(final String fileName, final String outputConfigurationName, final CharSequence contents) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteFile(final String fileName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteFile(final String fileName, final String outputConfigurationName) {
        throw new UnsupportedOperationException();
    }

    @Override
    public URI getURI(final String path, final String outputConfiguration) {
        throw new UnsupportedOperationException();
    }

    @Override
    public URI getURI(final String path) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void generateFile(final String fileName, final String outputCfgName, final InputStream content) throws RuntimeIOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void generateFile(final String fileName, final InputStream content) throws RuntimeIOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream readBinaryFile(final String fileName, final String outputCfgName) throws RuntimeIOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public InputStream readBinaryFile(final String fileName) throws RuntimeIOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence readTextFile(final String fileName, final String outputCfgName) throws RuntimeIOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public CharSequence readTextFile(final String fileName) throws RuntimeIOException {
        throw new UnsupportedOperationException();
    }

    public void addStore(final URI file, final OutputStream outputStream) {
        store.put(file, outputStream);
    }

    public OutputStream getOutputStream(final URI file) {
        return store.get(file);
    }
}
