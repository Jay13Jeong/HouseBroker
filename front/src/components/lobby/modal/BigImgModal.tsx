import React from "react";
import { useRecoilValue, useResetRecoilState } from "recoil";
import { bigImgModalState } from "../../../common/states/recoilModalState";
import ModalBase from "../../modal/ModalBase";
import "react-confirm-alert/src/react-confirm-alert.css";
import "./../../../assets/confirm-alert.css";

const BigImgModal: React.FC = () => {
   
  const showModal = useRecoilValue(bigImgModalState);
  const resetState = useResetRecoilState(bigImgModalState);

  const handleCloseModal = () => {
    resetState();
  };

  return (
    <ModalBase open={showModal.show} onClose={handleCloseModal}>
      <div style={{maxWidth: '100%', maxHeight: '90vh', overflow: 'scroll'}}>
        <img 
            src={showModal.imgUrl} 
            alt="bigImage"
            style={{ width: '100%', height: 'auto' }}
            onClick={handleCloseModal}
            onDragStart={e => e.preventDefault()}
        />
      </div>
    </ModalBase>
  );
};

export default BigImgModal;