library(readr)
library(ggplot2)
library(dplyr)
library(stringr)
library(tidyr)
library(forcats)
library(scales)

# Directory di output

out_dir <- ""
 ## inserire qui la directory di output

# Funzione per convertire in nanosecondi
to_ns <- function(times) {
  # pattern: cattura numero seguito dall'unità
  pattern <- "(\\d+)(ms|ns|s|m)"
  
  sapply(times, function(t) {
    matches <- str_match_all(t, pattern)[[1]]
    total <- 0
    for(i in seq_len(nrow(matches))) {
      value <- as.numeric(matches[i,2])
      unit  <- matches[i,3]
      total <- total + switch(unit,
                              m  = value*60*1e9,
                              s  = value*1e9,
                              ms = value*1e6,
                              ns = value)
    }
    total
  })
}

# Importa il file CSV.  ## inserire qui il path del file CSV
data <- read_csv("results.csv")
  

# Controlla struttura e tipi delle colonne
str(data)

# Controlla se ci sono valori mancanti
colSums(is.na(data))

# Controlla se ci sono righe duplicate
sum(duplicated(data))

# Aggiungi colonna "time_ns"
data <- data %>%
  mutate(time_ns = to_ns(Tempo))

data <- data %>%
  mutate(Categoria_Griglia = recode_factor(
    Categoria_Griglia,
    "tipoGriglia" = "TipoGriglia"
  ))
data <- data %>%
  rename("Landmark" = "Celle_Di_Frontiera")

#######################
#FILTRO
#######################

data_filtered <- data %>% 
                  filter(Timeout == "FALSE" &
                      Destinazione_Irraggiungibile == "FALSE")

####################
#Ordinamento
####################

data_filtered$Compito_Tre <- factor(data_filtered$Compito_Tre, levels = c(
                                                        "DEFAULT",
                                                        "PERFORMANCE_SORTED_FRONTIERA",
                                                        "PERFORMANCE_CONDIZIONE_RAFFORZATA",
                                                        "PERFORMANCE_NO_CACHE",
                                                        "PERFORMANCE_SVUOTA_FRONTIERA",
                                                        "PERFORMANCE_CACHE",
                                                        "PERFORMANCE_NO_CONDIZIONE_RAFFORZATA",
                                                        "PERFORMANCE",
                                                        "PERFORMANCE_FULL",
                                                        "PERFORMANCE_NO_SORTED_FRONTIERA"))



###################
##GRAFO 1: x= Frontiere, Y= tempo, Color = CDF x Landmark
p1 <- ggplot(data_filtered, aes(x = Media_Celle_Frontiera, y = time_ns, color = CDF_Per_Landmark)) +
  geom_point(alpha = 0.7) +
  scale_color_viridis_c(option = "plasma", trans = "log",  labels = label_scientific()) +
  geom_smooth(method = "lm", se = FALSE, color = "red") +  # retta di regressione
  scale_x_log10(
    breaks = c(1,2,3,4,5,10, 100, 1000, 10000, 20000, 50000, 75000,250000, 500000, 1000000, 2000000, 4000000),
    labels = c(1,2,3,4,5,10, 100, 1000, 10000, 20000, 50000, 75000,250000, 500000, 1000000, 2000000, 4000000))+
  scale_y_log10(
    breaks = c(100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000, 60000000000, 120000000000, 240000000000, 480000000000),
    labels = c("100 ns", "1 μs", "10 μs", "100 μs", "1 ms", "10 ms", "100 ms", "1 s", "10 s", "1 min","2 min", "4 min", "8 min")
  ) +
  labs(title = "Distribuzione dei tempi per totale numero di Celle di Frontiera",
       x = "Numero di frontiere considerate", y = "Tempo", color = "CDF per Landmark") +
  theme_linedraw() +
  theme(axis.text.x = element_text(angle = 45, hjust = 1),
        legend.text = element_text(angle = 45, hjust = 1, size = 10),
        legend.position = "bottom",
        legend.title = element_text(size = 10, vjust = 1),
        legend.key.size = unit(1, "cm"),
        panel.grid.minor = element_blank())
ggsave(file.path(out_dir, "plot_Frontiere_Tempo_CDFxL.png"), p1, width=12, height=6, dpi=300)


##GRAFO 2: x= Frontiere, Y= tempo, Color = LandMark
p2 <- ggplot(data_filtered, aes(x = Media_Celle_Frontiera, y = time_ns, color = Landmark)) +
  geom_point(alpha = 0.7) +
  scale_color_viridis_c(option = "plasma", trans = "log",  labels = label_scientific()) +
  geom_smooth(method = "lm", se = FALSE, color = "red") +  # retta di regressione
  scale_x_log10(
    breaks = c(1,2,3,4,5,10, 100, 1000, 10000, 20000, 50000, 75000,250000, 500000, 1000000, 2000000, 4000000),
    labels = c(1,2,3,4,5,10, 100, 1000, 10000, 20000, 50000, 75000,250000, 500000, 1000000, 2000000, 4000000))+
  scale_y_log10(
    breaks = c(100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000, 60000000000, 120000000000, 240000000000, 480000000000),
    labels = c("100 ns", "1 μs", "10 μs", "100 μs", "1 ms", "10 ms", "100 ms", "1 s", "10 s", "1 min","2 min", "4 min", "8 min")
  ) +
  labs(title = "Distribuzione dei tempi per totale numero di Celle di Frontiera",
       x = "Numero di frontiere considerate", y = "Tempo", color = "Landmark") +
  theme_linedraw() +
  theme(axis.text.x = element_text(angle = 45, hjust = 1),
        legend.text = element_text(angle = 45, hjust = 1, size = 10),
        legend.position = "bottom",
        legend.title = element_text(size = 10, vjust = 1),
        legend.key.size = unit(1, "cm"),
        panel.grid.minor = element_blank())
ggsave(file.path(out_dir, "plot_Frontiere_Tempo_Landmark.png"), p2, width=12, height=6, dpi=300)

##GRAFO 3: x= CDF x Landmark, y= tempo, Color= Frontiere
p3 <- ggplot(data_filtered, aes(x = CDF_Per_Landmark , y = time_ns, color = Media_Celle_Frontiera)) +
  geom_point(alpha = 0.7) +
  scale_color_viridis_c(option = "plasma", trans = "log",  labels = label_scientific()) +
  geom_smooth(method = "lm", se = FALSE, color = "red") +  # retta di regressione
  scale_x_log10(
    breaks = c(1,2,3,4,5,10, 100, 1000, 10000, 20000, 50000, 75000,250000, 500000, 1000000, 2000000, 4000000),
    labels = c(1,2,3,4,5,10, 100, 1000, 10000, 20000, 50000, 75000,250000, 500000, 1000000, 2000000, 4000000))+
  scale_y_log10(
    breaks = c(100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000, 60000000000, 120000000000, 240000000000, 480000000000),
    labels = c("100 ns", "1 μs", "10 μs", "100 μs", "1 ms", "10 ms", "100 ms", "1 s", "10 s", "1 min","2 min", "4 min", "8 min")
  ) +
  labs(title = "Distribuzione dei tempi per totale numero di CDF per Landmark",
       x = "CDF per Landmark", y = "Tempo", color = "Celle di Frontiera") +
  theme_linedraw() +
  theme(axis.text.x = element_text(angle = 45, hjust = 1),
        legend.text = element_text(angle = 45, hjust = 1, size = 10),
        legend.position = "bottom",
        legend.title = element_text(size = 10, vjust = 1),
        legend.key.size = unit(1, "cm"),
        panel.grid.minor = element_blank())
ggsave(file.path(out_dir, "plot_CDFxL_Tempo_Frontiere.png"), p3, width=12, height=6, dpi=300)

##GRAFO 4: x= CDF x Landmark, y= tempo, Color= Landmark
p4 <- ggplot(data_filtered, aes(x = CDF_Per_Landmark , y = time_ns, color = Landmark)) +
  geom_point(alpha = 0.7) +
  scale_color_viridis_c(option = "plasma", trans = "log",  labels = label_scientific()) +
  geom_smooth(method = "lm", se = FALSE, color = "red") +  # retta di regressione
  scale_x_log10(
    breaks = c(1,2,3,4,5,10, 100, 1000, 10000, 20000, 50000, 75000,250000, 500000, 1000000, 2000000, 4000000),
    labels = c(1,2,3,4,5,10, 100, 1000, 10000, 20000, 50000, 75000,250000, 500000, 1000000, 2000000, 4000000))+
  scale_y_log10(
    breaks = c(100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000, 60000000000, 120000000000, 240000000000, 480000000000),
    labels = c("100 ns", "1 μs", "10 μs", "100 μs", "1 ms", "10 ms", "100 ms", "1 s", "10 s", "1 min","2 min", "4 min", "8 min")
  ) +
  labs(title = "Distribuzione dei tempi per totale numero di CDF per Landmark",
       x = "CDF per Landmark", y = "Tempo", color = "Landmark") +
  theme_linedraw() +
  theme(axis.text.x = element_text(angle = 45, hjust = 1),
        legend.text = element_text(angle = 45, hjust = 1, size = 10),
        legend.position = "bottom",
        legend.title = element_text(size = 10, vjust = 1),
        legend.key.size = unit(1, "cm"),
        panel.grid.minor = element_blank())
ggsave(file.path(out_dir, "plot_CDFxL_Tempo_Landmark.png"), p4, width=12, height=6, dpi=300)


##GRAFO 5 BOXPLOT x= modalità di esecuzione, Y= tempo, Color = Categoria Griglia
p5 <- ggplot(data_filtered, aes(x = Compito_Tre, y = time_ns, fill = Categoria_Griglia)) +
  geom_boxplot(alpha = 0.7) +
  stat_summary(aes(group=Categoria_Griglia, color=Categoria_Griglia), 
               fun = median, geom = "line", linewidth = 1) +
  scale_y_log10(
    breaks = c(100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000,60000000000),
    labels = c("100 ns", "1 μs", "10 μs", "100 μs", "1 ms", "10 ms", "100 ms", "1 s", "10 s", "1 min")
  ) +
  theme_linedraw() + #linedraw
  theme(axis.text.x = element_text(angle = 35, hjust = 1, size=8), axis.text.y = element_text(size = 8),
        legend.position = "bottom",
        legend.direction = "horizontal",
        legend.text = element_text(size = 14),
        legend.title = element_text(size = 16)) +
  labs(title = "Medie dei tempi di esecuzione delle differenti Categorie di Griglia",
       x = "Modalità di esecuzione di Compito_Tre", y = "Tempo")

ggsave(file.path(out_dir, "plot_BoxPlot.png"), p5, width=12, height=8, dpi=300)

##GRAFO 6 LINES x= modalità di esecuzione, Y= tempo, Color = Categoria Griglia
p6 <- ggplot(data_filtered, aes(x = Compito_Tre, y = time_ns, fill = Categoria_Griglia)) +
  geom_point(alpha = 0.0) +
  stat_summary(aes(group=Categoria_Griglia, color=Categoria_Griglia), 
               fun = median, geom = "line", linewidth = 1) +
  scale_y_log10(
    breaks = c(100, 1000, 10000, 100000, 1000000, 10000000, 100000000, 1000000000, 10000000000,60000000000),
    labels = c("100 ns", "1 μs", "10 μs", "100 μs", "1 ms", "10 ms", "100 ms", "1 s", "10 s", "1 min")
  ) +
  theme_linedraw() + #linedraw
  theme(axis.text.x = element_text(angle = 35, hjust = 1, size=8), axis.text.y = element_text(size = 8),
        legend.position = "bottom",
        legend.direction = "horizontal",
        legend.text = element_text(size = 14),
        legend.title = element_text(size = 16)) +
  labs(title = "Medie dei tempi di esecuzione delle differenti Categorie di Griglia",
       x = "Modalità di esecuzione di Compito_Tre", y = "Tempo")

ggsave(file.path(out_dir, "plot_tempi_Linee.png"), p6, width=12, height=8, dpi=300)



##

