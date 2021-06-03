package proj;

import java.util.Objects;

public class FileEntity {
    private String fileName;
    private String filePath;

    public FileEntity(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    @Override
    public String toString() {
        return "FileEntity{" +
                "fileName='" + fileName + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileEntity that = (FileEntity) o;
        return Objects.equals(fileName, that.fileName) &&
                Objects.equals(filePath, that.filePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileName, filePath);
    }
}
