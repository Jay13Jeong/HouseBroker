all : dir down
	docker compose up --build -d

fore : dir
	docker compose up --build

develop : dir down
	docker compose -f develop.yaml up --build 

dir :
	mkdir -p $(HOME)/kiwi_data/upload_images
	mkdir -p $(HOME)/kiwi_data/DB
	mkdir -p $(HOME)/kiwi_data/DB2

clean : down
	rm -rf ${HOME}/kiwi_data

down : 
	docker compose -f develop.yaml down
	docker compose -f docker-compose.yaml down

fclean : clean
	docker rmi -f $(shell docker images -a -q)
	docker system prune -f

re : clean all

.PHONY: all fore develop dir down clean fclean re
