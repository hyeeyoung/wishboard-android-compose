package com.hyeeyoung.wishboard.domain.model.folder

/** 폴더 대표 사진 후보. 폴더에 담긴 아이템마다 첫 번째 이미지가 하나씩 내려온다. */
data class FolderThumbnailImage(
    val itemId: Long,
    val itemImageId: Long,
    val imageUrl: String,
    /** 현재 적용된 대표 사진인지 여부. 지정하지 않았으면 기본 대표 사진(목록 맨 앞)이 true */
    val isSelected: Boolean,
)
