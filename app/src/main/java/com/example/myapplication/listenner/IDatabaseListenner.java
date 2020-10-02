package com.example.myapplication.listenner;


import com.example.myapplication.model.Song;

/**BachNN
 * interface này dung để callbach về main để thực hiện các câu lệnh truy vẫn
 * trên cơ sở lưu liệu
 */
public interface IDatabaseListenner {
    //BachNN : thêm 1 bài hát bào CSDL
    void addFavouriteMusic(Song song);
    //BachNN : xóa 1 bài hát bào CSDL
    void deleteFavouriteMusic(int id);
    //BachNN : lấy all các bài hát yêu thích.
    void getAllFavouriteMusic();
}
