import React, { useEffect, useState } from 'react';
import Avatar from '@mui/material/Avatar';
import ArrowCircleLeftIcon from '@mui/icons-material/ArrowCircleLeft';
import ArrowCircleRightIcon from '@mui/icons-material/ArrowCircleRight';
import { IconButton } from '@mui/material';
import { useRecoilValue, useResetRecoilState, useSetRecoilState } from 'recoil';
import { selectedImgCardIndexState } from '../../common/states/recoilModalState';

const ImageCard: React.FC<{ images: string[] }> = ({ images }) => {
    const [scrollInterval, setScrollInterval] = useState<NodeJS.Timer | null>(null);
    const setIndexState = useSetRecoilState(selectedImgCardIndexState);
    const resetState = useResetRecoilState(selectedImgCardIndexState);
    const indexState = useRecoilValue(selectedImgCardIndexState);

    useEffect(() => {
        setScrollInterval(null);
        resetState();
    },[])

    const handleScroll = (direction: 'left' | 'right') => {
      const galleryContainer = document.querySelector('.gallery-container');
      if (galleryContainer) {
        const scrollAmount = 5;
        if (direction === 'left') {
          galleryContainer.scrollLeft -= scrollAmount;
        } else {
          galleryContainer.scrollLeft += scrollAmount;
        }
      }
    };
  
    const handleMouseDown = (direction: 'left' | 'right') => {
      const interval = setInterval(() => {
        handleScroll(direction);
      }, 5);
      setScrollInterval(interval);
    };
  
    const handleMouseUp = () => {
      if (scrollInterval !== null) {
        clearInterval(scrollInterval);
        setScrollInterval(null);
      }
    };
  
    return (
        <div style={{ position: 'relative', width: '100%', height: 350 }}>
        <div className="gallery-container" style={{ display: 'flex', overflowX: 'scroll', width: '100%', height: '100%' }}>
        {images.map((image, index) => (
            <div
            key={index}
            style={{
                width: 'auto',
                flexShrink: 0,
                margin: 10,
                cursor: 'grab',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                border: indexState.index === index ? '10px solid green' : '2px solid black',
                borderRadius: '10px',
            }}
            >
            <Avatar 
                src={image} 
                alt="estate_image" 
                variant="rounded" 
                sx={{ width: '100%', height: '100%' }} 
                onClick={() => setIndexState({index : index})}
                onDragStart={e => e.preventDefault()}
            />
            </div>
        ))}
        </div>
        <div style={{ position: 'absolute', top: '50%', transform: 'translateY(-50%)', left: '10px' }}>
        <ArrowCircleLeftIcon
            onMouseDown={() => handleMouseDown('left')}
            onMouseUp={handleMouseUp}
            onMouseLeave={handleMouseUp}
            sx={{ fontSize: 80, cursor: 'pointer' }}
        />
        </div>
        <div style={{ position: 'absolute', top: '50%', transform: 'translateY(-50%)', right: '10px' }}>
        <ArrowCircleRightIcon
            onMouseDown={() => handleMouseDown('right')}
            onMouseUp={handleMouseUp}
            onMouseLeave={handleMouseUp}
            sx={{ fontSize: 80, cursor: 'pointer' }}
        />
        </div>
        </div>
    );
};

export default ImageCard;