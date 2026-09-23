import { PrismaClient } from 'prisma-client';

const prisma = new PrismaClient();

async function main() {
  console.log('Seeding database...');

  // 1. Insertar Frases Aleatorias (Quotes)
  const quotes = [
    "¡A jugar!",
    "¿Listo para el reto?",
    "¡Demuestra lo que sabes!",
    "¡A ganar!",
    "¡Que comience el juego!"
  ];

  for (const text of quotes) {
    await prisma.quote.create({
      data: { text }
    });
  }

  // 2. Insertar SuperPoderes (de los 13 personajes)
  const superPowers = [
    {
      id: 'thief',
      name: 'Ladrón Astuto',
      description: 'Ve la respuesta de tu rival y cópiala. Si aciertas, le robas la mitad de sus puntos.',
      effectType: 'STEAL_POINTS',
      powerType: 'OFFENSIVE'
    },
    {
      id: 'fifty_fifty',
      name: '50/50',
      description: 'Elimina las opciones incorrectas para que sea más fácil adivinar.',
      effectType: 'REMOVE_OPTION',
      powerType: 'TACTICAL'
    },
    {
      id: 'brute_force',
      name: 'Fuerza Bruta',
      description: 'Gana el DOBLE de puntos si aciertas, pero pierdes puntos si fallas. ¡Arriésgate!',
      effectType: 'DOUBLE_OR_NOTHING',
      powerType: 'OFFENSIVE'
    },
    {
      id: 'peacock_hypnosis',
      name: 'Hipnosis de Plumas',
      description: 'Lanza una ilusión que intercambia físicamente la posición de las opciones en las pantallas de tus rivales.',
      effectType: 'SCRAMBLE_OPTIONS',
      powerType: 'TACTICAL'
    },
    {
      id: 'chameleon_mimic',
      name: 'Mimetismo',
      description: 'Copia instantáneamente el poder del rival seleccionado, y bloquea pasivamente ataques directos hacia ti.',
      effectType: 'COPY_POWER',
      powerType: 'DEFENSIVE'
    },
    {
      id: 'bat_blackout',
      name: 'Apagón',
      description: 'Oculta el texto de la pregunta a todos los demás jugadores durante los primeros 4 segundos.',
      effectType: 'BLACKOUT',
      powerType: 'OFFENSIVE'
    },
    {
      id: 'dragon_burn',
      name: 'Tierra Quemada',
      description: 'Quema y oculta una opción al azar en las pantallas de todos tus rivales. Aumenta sus pérdidas si se equivocan.',
      effectType: 'BURN_OPTION',
      powerType: 'OFFENSIVE'
    },
    {
      id: 'nine_lives',
      name: '7 Vidas',
      description: 'Si te equivocas, recibes la mitad de los puntos como premio de consolación. Un seguro de vida.',
      effectType: 'SECOND_CHANCE',
      powerType: 'DEFENSIVE'
    },
    {
      id: 'speed_boost',
      name: 'Impulso',
      description: 'Si aciertas, ganas +50% de puntos extra por ser veloz.',
      effectType: 'SPEED_BOOST',
      powerType: 'TACTICAL'
    },
    {
      id: 'loyalty',
      name: 'Lealtad',
      description: 'Ve la respuesta de tu amigo en pantalla para que puedan ayudarse a responder.',
      effectType: 'SHARE_POINTS',
      powerType: 'DEFENSIVE'
    },
    {
      id: 'gallo_silence',
      name: 'Rey del Gallinero',
      description: 'Silencia a todos los demás jugadores. Nadie podrá usar sus poderes en esta ronda.',
      effectType: 'SILENCE_ALL',
      powerType: 'SPECIAL'
    },
    {
      id: 'duck_water',
      name: 'Espejismo Acuático',
      description: 'Inunda la pantalla de tus rivales. Sus opciones de respuesta se ondularán mágicamente como si estuvieran bajo el agua, dificultando enormemente su lectura.',
      effectType: 'WATER_MIRAGE',
      powerType: 'OFFENSIVE'
    },
    {
      id: 'medusa_toxin',
      name: 'Neuro-Toxina',
      description: 'Congela el reloj del oponente de un color tóxico y mezcla visualmente las letras de sus respuestas durante los últimos 3 segundos.',
      effectType: 'TOXIN',
      powerType: 'OFFENSIVE'
    }
  ];

  for (const power of superPowers) {
    await prisma.superPower.upsert({
      where: { id: power.id },
      update: power,
      create: power,
    });
  }

  // 3. Insertar Avatares
  const avatars = [
    {
      id: 'fox',
      name: 'Zorro',
      phrase: '¡A la victoria con astucia!',
      superPowerId: 'thief',
      isMythic: false,
      isUnlocked: true
    },
    {
      id: 'owl',
      name: 'Búho',
      phrase: 'La sabiduría es nuestra mejor arma.',
      superPowerId: 'fifty_fifty',
      isMythic: false,
      isUnlocked: true
    },
    {
      id: 'bear',
      name: 'Oso',
      phrase: '¡Fuerza y concentración para cada pregunta!',
      superPowerId: 'brute_force',
      isMythic: false,
      isUnlocked: true
    },
    {
      id: 'peacock',
      name: 'Pavo Real',
      phrase: '¡Belleza y distracciones visuales!',
      superPowerId: 'peacock_hypnosis',
      isMythic: true,
      isUnlocked: false
    },
    {
      id: 'chameleon',
      name: 'Camaleón',
      phrase: 'Me adapto y sobrevivo.',
      superPowerId: 'chameleon_mimic',
      isMythic: true,
      isUnlocked: false
    },
    {
      id: 'bat',
      name: 'Murciélago',
      phrase: 'La oscuridad es mi aliada.',
      superPowerId: 'bat_blackout',
      isMythic: true,
      isUnlocked: false
    },
    {
      id: 'dragon',
      name: 'Dragón',
      phrase: 'Fuego y destrucción a los rivales.',
      superPowerId: 'dragon_burn',
      isMythic: true,
      isUnlocked: false
    },
    {
      id: 'cat',
      name: 'Gato',
      phrase: 'Curiosidad y 7 vidas para fallar.',
      superPowerId: 'nine_lives',
      isMythic: false,
      isUnlocked: true
    },
    {
      id: 'rabbit',
      name: 'Conejo',
      phrase: '¡Velocidad máxima en las respuestas!',
      superPowerId: 'speed_boost',
      isMythic: false,
      isUnlocked: true
    },
    {
      id: 'dog',
      name: 'Perro',
      phrase: '¡El mejor amigo de tus notas!',
      superPowerId: 'loyalty',
      isMythic: false,
      isUnlocked: true
    },
    {
      id: 'gallo',
      name: 'Gallo',
      phrase: '¡A despertar y reinar en el gallinero!',
      superPowerId: 'gallo_silence',
      isMythic: true,
      isUnlocked: false
    },
    {
      id: 'duck',
      name: 'PATO',
      phrase: '¡Al agua patos!',
      superPowerId: 'duck_water',
      isMythic: true,
      isUnlocked: true
    },
    {
      id: 'medusa',
      name: 'Medusa Astral',
      phrase: 'No me mires o te congelarás.',
      superPowerId: 'medusa_toxin',
      isMythic: true,
      isUnlocked: false
    }
  ];

  for (const avatar of avatars) {
    await prisma.avatar.upsert({
      where: { id: avatar.id },
      update: avatar,
      create: avatar,
    });
  }

  // 4. Insertar Categorías
  const categories = [
    { id: 'GENERAL_CULTURE', name: 'Cultura General', description: 'Preguntas sobre historia, geografía y arte.' },
    { id: 'TECHNOLOGY', name: 'Tecnología', description: 'Preguntas sobre programación, hardware y software.' },
    { id: 'DISTRIBUTED_SYSTEMS', name: 'Sistemas Distribuidos', description: 'Conceptos avanzados de arquitectura de software.' }
  ];

  for (const category of categories) {
    await prisma.category.upsert({
      where: { id: category.id },
      update: category,
      create: category,
    });
  }

  // 5. Insertar un Quiz de Prueba
  // Para que esto funcione sin fallar por las foreign keys, necesitamos un User dummy
  const dummyUser = await prisma.user.upsert({
    where: { email: 'admin@quizsync.com' },
    update: {},
    create: {
      email: 'admin@quizsync.com',
      name: 'Admin',
      avatarId: 'owl'
    }
  });

  const sampleQuiz = await prisma.quiz.create({
    data: {
      title: 'Historia de la Computación',
      description: 'Demuestra tus conocimientos sobre los orígenes de la informática.',
      categoryId: 'TECHNOLOGY',
      authorId: dummyUser.id,
      questions: {
        create: [
          {
            text: '¿Quién es considerado el padre de la computación?',
            timeLimit: 15,
            maxPoints: 1000,
            options: {
              create: [
                { text: 'Alan Turing', isCorrect: true },
                { text: 'Bill Gates', isCorrect: false },
                { text: 'Steve Jobs', isCorrect: false },
                { text: 'Mark Zuckerberg', isCorrect: false }
              ]
            }
          },
          {
            text: '¿En qué año se lanzó el primer iPhone?',
            timeLimit: 10, // Menos tiempo porque es más fácil
            maxPoints: 800,
            options: {
              create: [
                { text: '2005', isCorrect: false },
                { text: '2007', isCorrect: true },
                { text: '2008', isCorrect: false },
                { text: '2010', isCorrect: false }
              ]
            }
          }
        ]
      }
    }
  });

  console.log('Seeding finished.');
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(async () => {
    await prisma.$disconnect();
  });
