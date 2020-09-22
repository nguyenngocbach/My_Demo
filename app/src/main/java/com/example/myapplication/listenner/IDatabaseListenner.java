package com.example.myapplication.listenner;


import com.example.myapplication.Model.Song;

/**BachNN
 * interface này dung để callbach về main để thực hiện các câu lệnh truy vẫn
 * trên cơ sở lưu liệu
 */
public interface IDatabaseListenner {
    // thêm 1 bài hát bào CSDL
    void addFavouriteMusic(Song song);
    // xóa 1 bài hát bào CSDL
    void deleteFavouriteMusic(int id);
    // lấy all các bài hát yêu thích.
    void getAllFavouriteMusic();
}
