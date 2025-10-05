package com.tclk.fileupload.repository;

import com.tclk.fileupload.entity.FileData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FileDataRepository extends JpaRepository<FileData,Long> {

    @Query(value = "SELECT * FROM FILE_DATA WHERE file_name = :name LIMIT 1", nativeQuery = true)
    public FileData findAllSortedByNameUsingNative(@Param("name") String name);


    Optional<FileData> findByName(String fileName);
}
