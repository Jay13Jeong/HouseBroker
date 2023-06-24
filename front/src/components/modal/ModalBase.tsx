import React from "react";
import { Resetter } from "recoil";
import { Modal, Box, IconButton } from "@mui/material";
import CloseIcon from '@mui/icons-material/Close';

import * as S from './Modal.style'

export interface ModalBaseProps {
  children: React.ReactNode;
  z_index?: number;
  reset?: Resetter;
  closeButton?: boolean;
  open: boolean;
  onClose?: () => void;
}

function ModalBase(props: ModalBaseProps) {
  return (
    <Modal open={props.open} onClose={props.onClose}>
      <Box sx={S.modalSx}>
        {props.closeButton && props.reset ? (
          <IconButton
            sx={S.closeButtonSx}
            onClick={() => props.reset?.()}
          >
            <CloseIcon />
          </IconButton>
        ) : null}
        {props.children}
      </Box>
    </Modal>
  );
}

export default ModalBase;