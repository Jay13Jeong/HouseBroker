import { styled, Button } from '@mui/material/';

export const DefaultButton2 = styled(Button)(({ theme }) => ({
    backgroundColor: theme.palette.secondary.main,
    color: theme.palette.text.primary,
    margin: '1rem',
    '&:hover': {
        backgroundColor: theme.palette.info.light,
    },
    a : {
        textDecoration: 'none',
        color: 'success',
    }
  }));