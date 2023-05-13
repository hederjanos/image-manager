package hu.ponte.hr.services.storagehandler;

import java.util.concurrent.CompletableFuture;

/**
 * Interface for handling data storage.
 */
public interface StorageHandler {

    /**
     * Initialize required fields.
     */
    void init();

    /**
     * Upload data to storage. The uploading process will be executed on a separated thread.
     *
     * @param data     data to be uploaded
     * @param fileName to be bind to {@code data}
     * @return a {@link Boolean} instance wrapped in {@link CompletableFuture} object.
     */
    CompletableFuture<Boolean> uploadAFile(byte[] data, String fileName);

    /**
     * Download data from a storage by name.The downloading process will be executed on a separated thread.
     *
     * @param fileName the name of the file to be found
     * @return a byte array instance wrapped in {@link CompletableFuture} object.
     */
    CompletableFuture<byte[]> downloadAFile(String fileName);

}
