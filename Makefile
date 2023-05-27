all :
	docker-compose up --build

clean :
	docker-compose -f docker-compose.yaml down
	rm -rf ${HOME}/kiwi_data
	mv -f ./backend/avatars/default.jpeg ./
	rm -f ./backend/avatars/*
	mv -f ./default.jpeg ./backend/avatars/

mariadb :
	docker-compose -f develop.yaml up --build 

fclean : clean
	docker rmi -f $(shell docker images -a -q)
	docker system prune -f

re : clean all

.PHONY: all clean fclean re