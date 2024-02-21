all : dir
	docker compose up --build -d

fore : dir
	docker compose up --build

dir :
	mkdir -p $(HOME)/kiwi_data/upload_images
	mkdir -p $(HOME)/kiwi_data/DB

clean :
	docker compose -f docker-compose.yaml down
	rm -rf ${HOME}/kiwi_data

develop : dir
	docker compose -f develop.yaml up --build 

fclean : clean
	docker rmi -f $(shell docker images -a -q)
	docker system prune -f

re : clean all

.PHONY: all clean fclean re
